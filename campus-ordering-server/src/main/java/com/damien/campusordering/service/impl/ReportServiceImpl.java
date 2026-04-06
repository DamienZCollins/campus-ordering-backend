package com.damien.campusordering.service.impl;

import com.damien.campusordering.entity.Orders;
import com.damien.campusordering.mapper.OrderMapper;
import com.damien.campusordering.mapper.UserMapper;
import com.damien.campusordering.service.ReportService;
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
        //存放日期集合
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.isAfter(end)) {
            //日期计算
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //一次性查询所有营业额数据
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        List<Map<String, Object>> turnoverDataList = orderMapper.getTurnoverByDateRange(beginTime, endTime, Orders.COMPLETED);

        //将查询结果转换为Map，key为日期，value为营业额
        Map<String, Double> turnoverMap = new HashMap<>();
        for (Map<String, Object> data : turnoverDataList) {
            String date = data.get("date").toString();
            Double turnover = ((Number) data.get("turnover")).doubleValue();
            turnoverMap.put(date, turnover);
        }

        //存放营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Double turnover = turnoverMap.getOrDefault(date.toString(), 0.0);
            turnoverList.add(turnover);
        }

        //封装返回结果
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
        //存放日期集合
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.isAfter(end)) {
            //日期计算
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //一次性查询所有用户统计数据
        LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);

        List<Map<String, Object>> userDataList = userMapper.getUserStatisticsByDateRange(beginTime, endTime);

        //将查询结果转换为Map，key为日期，value为用户数据
        Map<String, Map<String, Object>> userStatsMap = new HashMap<>();
        for (Map<String, Object> data : userDataList) {
            String date = data.get("date").toString();
            userStatsMap.put(date, data);
        }

        //存放新增用户和总用户
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        //遍历每一天，填充数据
        for (LocalDate date : dateList) {
            Map<String, Object> stats = userStatsMap.get(date.toString());
            if (stats != null) {
                Integer newUserCount = ((Number) stats.get("newUserCount")).intValue();
                Integer totalUserCount = ((Number) stats.get("totalUserCount")).intValue();
                newUserList.add(newUserCount);
                totalUserList.add(totalUserCount);
            } else {
                //该天没有新用户，保持前一天的总数
                Integer prevTotal = totalUserList.isEmpty() ? 0 : totalUserList.get(totalUserList.size() - 1);
                newUserList.add(0);
                totalUserList.add(prevTotal);
            }
        }

        //封装返回结果
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }
}
