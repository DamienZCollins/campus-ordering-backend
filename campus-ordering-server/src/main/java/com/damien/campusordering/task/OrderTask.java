package com.damien.campusordering.task;

import com.damien.campusordering.entity.Orders;
import com.damien.campusordering.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("开始处理超时订单:{}", LocalDateTime.now());
        //查询是否有超时订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (orderList != null && !orderList.isEmpty()) {
            List<Long> orderIds = new ArrayList<>();
            for (Orders orders : orderList) {
                orderIds.add(orders.getId());
            }
            orderMapper.updateBatchByStatusAndIds(Orders.CANCELLED, "支付超时", LocalDateTime.now(), orderIds);
            log.info("共处理{}笔超时订单", orderList.size());
        }
    }
}
