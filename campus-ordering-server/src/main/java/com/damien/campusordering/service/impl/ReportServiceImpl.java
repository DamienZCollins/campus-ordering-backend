package com.damien.campusordering.service.impl;

import com.damien.campusordering.entity.Orders;
import com.damien.campusordering.mapper.OrderMapper;
import com.damien.campusordering.service.ReportService;
import com.damien.campusordering.vo.TurnoverReportVO;
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
}
