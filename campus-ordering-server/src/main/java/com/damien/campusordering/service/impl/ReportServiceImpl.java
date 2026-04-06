package com.damien.campusordering.service.impl;

import com.damien.campusordering.entity.Orders;
import com.damien.campusordering.mapper.OrderMapper;
import com.damien.campusordering.mapper.UserMapper;
import com.damien.campusordering.service.ReportService;
import com.damien.campusordering.vo.OrderReportVO;
import com.damien.campusordering.vo.TurnoverReportVO;
import com.damien.campusordering.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
