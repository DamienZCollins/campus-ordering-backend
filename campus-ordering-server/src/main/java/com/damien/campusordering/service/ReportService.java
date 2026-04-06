package com.damien.campusordering.service;

import com.damien.campusordering.vo.OrderReportVO;
import com.damien.campusordering.vo.SalesTop10ReportVO;
import com.damien.campusordering.vo.TurnoverReportVO;
import com.damien.campusordering.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 营业额报表数据
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的用户数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户报表数据
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 订单报表数据
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的销量排行数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 销量排行报表数据
     */
    SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end);
}
