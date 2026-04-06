package com.damien.campusordering.service.impl;

import com.damien.campusordering.dto.GoodsSalesDTO;
import com.damien.campusordering.entity.Orders;
import com.damien.campusordering.mapper.OrderMapper;
import com.damien.campusordering.mapper.UserMapper;
import com.damien.campusordering.service.ReportService;
import com.damien.campusordering.vo.OrderReportVO;
import com.damien.campusordering.vo.SalesTop10ReportVO;
import com.damien.campusordering.vo.TurnoverReportVO;
import com.damien.campusordering.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 营业额报表数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        log.info("统计营业额，开始日期：{}，结束日期：{}", begin, end);
        // 先补齐时间区间内的所有日期
        List<LocalDate> dateList = buildDateList(begin, end);

        // 按日期区间一次性查出聚合结果
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        List<Map<String, Object>> turnoverDataList = orderMapper.getTurnoverByDateRange(beginTime, endTime, Orders.COMPLETED);

        // 将查询结果转成日期 -> 营业额的映射
        Map<String, Double> turnoverMap = new HashMap<>();
        for (Map<String, Object> data : turnoverDataList) {
            String date = data.get("date").toString();
            Double turnover = ((Number) data.get("turnover")).doubleValue();
            turnoverMap.put(date, turnover);
        }

        // 按日期顺序回填，没有数据的日期补 0
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Double turnover = turnoverMap.getOrDefault(date.toString(), 0.0);
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户报表数据
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        log.info("统计用户，开始日期：{}，结束日期：{}", begin, end);
        // 先补齐时间区间内的所有日期
        List<LocalDate> dateList = buildDateList(begin, end);

        // 按日期区间一次性查出用户统计数据
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        List<Map<String, Object>> userDataList = userMapper.getUserStatisticsByDateRange(beginTime, endTime);

        // 将查询结果转成日期 -> 统计数据的映射
        Map<String, Map<String, Object>> userStatsMap = new HashMap<>();
        for (Map<String, Object> data : userDataList) {
            String date = data.get("date").toString();
            userStatsMap.put(date, data);
        }

        // 回填每日新增用户和累计用户，没有新用户的日期沿用上一天总量
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map<String, Object> stats = userStatsMap.get(date.toString());
            if (stats != null) {
                Integer newUserCount = ((Number) stats.get("newUserCount")).intValue();
                Integer totalUserCount = ((Number) stats.get("totalUserCount")).intValue();
                newUserList.add(newUserCount);
                totalUserList.add(totalUserCount);
            } else {
                Integer prevTotal = totalUserList.isEmpty() ? 0 : totalUserList.get(totalUserList.size() - 1);
                newUserList.add(0);
                totalUserList.add(prevTotal);
            }
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 订单报表数据
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        log.info("统计订单，开始日期：{}，结束日期：{}", begin, end);
        // 先补齐时间区间内的所有日期
        List<LocalDate> dateList = buildDateList(begin, end);

        // 按日期区间一次性查出订单统计结果
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        List<Map<String, Object>> orderDataList = orderMapper.getOrderStatisticsByDateRange(beginTime, endTime);

        // 将查询结果转成日期 -> 统计数据的映射
        Map<String, Map<String, Object>> orderStatsMap = new HashMap<>();
        for (Map<String, Object> data : orderDataList) {
            String date = data.get("date").toString();
            orderStatsMap.put(date, data);
        }

        // 回填每日订单数和有效订单数，没有数据的日期补 0
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        for (LocalDate date : dateList) {
            Map<String, Object> stats = orderStatsMap.get(date.toString());
            if (stats != null) {
                Integer orderCount = ((Number) stats.get("totalOrderCount")).intValue();
                Integer validOrderCountDay = ((Number) stats.get("validOrderCount")).intValue();
                orderCountList.add(orderCount);
                validOrderCountList.add(validOrderCountDay);
                totalOrderCount += orderCount;
                validOrderCount += validOrderCountDay;
            } else {
                orderCountList.add(0);
                validOrderCountList.add(0);
            }
        }

        // 订单完成率按百分比返回，和接口设计保持一致
        double orderCompletionRate = totalOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount * 100;

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计指定时间区间内的销量排行数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 销量排行报表数据
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        log.info("统计销量TOP10，开始日期：{}，结束日期：{}", begin, end);
        // 将日期转换成完整的时间范围
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 只统计已完成订单中的商品销量
        // 一次性查出销量前10的商品数据
        List<GoodsSalesDTO> salesTop10List = orderMapper.getSalesTop10ByDateRange(beginTime, endTime, Orders.COMPLETED);

        // 拆分成前端需要的两个列表
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesTop10List) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    private List<LocalDate> buildDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        // 逐天生成完整日期区间
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dateList;
    }

    /**
     * 导出最近30天的运营数据报表为 Excel 文件
     * 使用模板填充方式生成报表
     *
     * @param response HTTP 响应对象，用于输出 Excel 文件流
     * @throws IOException 当写入响应流失败时抛出
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        // 1. 计算最近30天的日期范围
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate beginDate = endDate.minusDays(29);

        log.info("导出运营数据报表，日期范围：{} 至 {}", beginDate, endDate);

        // 2. 获取日期列表
        List<LocalDate> dateList = buildDateList(beginDate, endDate);
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        // 3. 查询营业额数据
        List<Map<String, Object>> turnoverDataList = orderMapper.getTurnoverByDateRange(beginTime, endTime, Orders.COMPLETED);
        Map<String, Double> turnoverMap = new HashMap<>();
        for (Map<String, Object> data : turnoverDataList) {
            String date = data.get("date").toString();
            Double turnover = ((Number) data.get("turnover")).doubleValue();
            turnoverMap.put(date, turnover);
        }

        // 4. 查询订单统计数据
        List<Map<String, Object>> orderDataList = orderMapper.getOrderStatisticsByDateRange(beginTime, endTime);
        Map<String, Map<String, Object>> orderStatsMap = new HashMap<>();
        for (Map<String, Object> data : orderDataList) {
            String date = data.get("date").toString();
            orderStatsMap.put(date, data);
        }

        // 5. 查询用户统计数据
        List<Map<String, Object>> userDataList = userMapper.getUserStatisticsByDateRange(beginTime, endTime);
        Map<String, Integer> newUserMap = new HashMap<>();
        for (Map<String, Object> data : userDataList) {
            String date = data.get("date").toString();
            Integer newUserCount = ((Number) data.get("newUserCount")).intValue();
            newUserMap.put(date, newUserCount);
        }

        // 6. 构建明细数据列表
        List<DetailData> detailDataList = new ArrayList<>();
        double totalTurnover = 0.0;
        int totalValidOrders = 0;
        int totalOrders = 0;
        int totalNewUsers = 0;

        for (LocalDate date : dateList) {
            String dateStr = date.toString();

            Double turnover = turnoverMap.getOrDefault(dateStr, 0.0);
            totalTurnover += turnover;

            Map<String, Object> orderStats = orderStatsMap.get(dateStr);
            int orderCount = 0;
            int validOrderCount = 0;
            double orderCompletionRate = 0.0;
            double averagePrice = 0.0;

            if (orderStats != null) {
                orderCount = ((Number) orderStats.get("totalOrderCount")).intValue();
                validOrderCount = ((Number) orderStats.get("validOrderCount")).intValue();
                totalOrders += orderCount;
                totalValidOrders += validOrderCount;

                if (orderCount > 0) {
                    orderCompletionRate = (double) validOrderCount / orderCount;
                }
                if (validOrderCount > 0) {
                    averagePrice = turnover / validOrderCount;
                }
            }

            Integer newUsers = newUserMap.getOrDefault(dateStr, 0);
            totalNewUsers += newUsers;

            DetailData detail = new DetailData();
            detail.setDate(dateStr);
            detail.setTurnover(String.format("%.2f", turnover));
            detail.setValidOrders(String.valueOf(validOrderCount));
            detail.setOrderCompletionRate(String.format("%.2f%%", orderCompletionRate * 100));
            detail.setAveragePrice(String.format("%.2f", averagePrice));
            detail.setNewUsers(String.valueOf(newUsers));
            detailDataList.add(detail);
        }

        // 7. 计算概览数据
        double totalOrderCompletionRate = totalOrders == 0 ? 0.0 : (double) totalValidOrders / totalOrders;
        double totalAveragePrice = totalValidOrders == 0 ? 0.0 : totalTurnover / totalValidOrders;

        OverviewData overview = new OverviewData();
        overview.setTurnover(String.format("%.2f", totalTurnover));
        overview.setOrderCompletionRate(String.format("%.2f%%", totalOrderCompletionRate * 100));
        overview.setNewUsers(String.valueOf(totalNewUsers));
        overview.setValidOrders(String.valueOf(totalValidOrders));
        overview.setAveragePrice(String.format("%.2f", totalAveragePrice));

        // 8. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("运营数据报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 9. 使用模板填充数据（自动匹配 template 目录下首个 xlsx，规避文件名编码差异）
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] templateResources = resolver.getResources("classpath*:template/*.xlsx");
        if (templateResources == null || templateResources.length == 0) {
            log.error("模板文件不存在：classpath*:template/*.xlsx");
            throw new IOException("模板文件不存在");
        }

        try (InputStream templateInputStream = templateResources[0].getInputStream();
             Workbook workbook = WorkbookFactory.create(templateInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 概览数据写入（模板固定位置）
            setCellValue(sheet, 3, 2, overview.getTurnover());            // C4
            setCellValue(sheet, 3, 4, overview.getOrderCompletionRate()); // E4
            setCellValue(sheet, 3, 6, overview.getNewUsers());            // G4
            setCellValue(sheet, 4, 2, overview.getValidOrders());         // C5
            setCellValue(sheet, 4, 4, overview.getAveragePrice());        // E5

            // 明细数据从第8行开始写入（模板第7行为表头）
            int startRowIndex = 7;
            for (int i = 0; i < detailDataList.size(); i++) {
                DetailData detail = detailDataList.get(i);
                int rowIndex = startRowIndex + i;
                setCellValue(sheet, rowIndex, 1, detail.getDate());                // B列
                setCellValue(sheet, rowIndex, 2, detail.getTurnover());            // C列
                setCellValue(sheet, rowIndex, 3, detail.getValidOrders());         // D列
                setCellValue(sheet, rowIndex, 4, detail.getOrderCompletionRate()); // E列
                setCellValue(sheet, rowIndex, 5, detail.getAveragePrice());        // F列
                setCellValue(sheet, rowIndex, 6, detail.getNewUsers());            // G列
            }

            workbook.write(response.getOutputStream());
        }
    }

    private void setCellValue(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }

    /**
     * 概览数据
     */
    @Data
    public static class OverviewData {
        private String turnover;
        private String orderCompletionRate;
        private String newUsers;
        private String validOrders;
        private String averagePrice;
    }

    /**
     * 明细数据
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailData {
        private String date;
        private String turnover;
        private String validOrders;
        private String orderCompletionRate;
        private String averagePrice;
        private String newUsers;
    }
}
