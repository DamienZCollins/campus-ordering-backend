package com.damien.campusordering.controller.admin;

import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.ReportService;
import com.damien.campusordering.vo.OrderReportVO;
import com.damien.campusordering.vo.SalesTop10ReportVO;
import com.damien.campusordering.vo.TurnoverReportVO;
import com.damien.campusordering.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 营业额统计数据
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("统计营业额：begin={}, end={}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户统计数据
     */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("统计用户：begin={}, end={}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 订单统计
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 订单统计数据
     */
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("统计订单：begin={}, end={}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }

    /**
     * 销量排行TOP10
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 销量排行数据
     */
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10Statistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("统计销量TOP10：begin={}, end={}", begin, end);
        return Result.success(reportService.getSalesTop10Statistics(begin, end));
    }

    /**
     * 导出运营数据报表
     *
     * @param response HTTP 响应对象
     * @throws IOException 当导出失败时抛出
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        log.info("导出运营数据报表");
        try {
            reportService.exportBusinessData(response);
        } catch (Exception e) {
            log.error("导出报表失败", e);
            // 如果响应尚未提交，返回错误信息
            if (!response.isCommitted()) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"code\":500,\"msg\":\"导出报表失败\",\"data\":null}");
            }
        }
    }
}
