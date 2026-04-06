package com.damien.campusordering.service;

import com.damien.campusordering.dto.OrdersPaymentDTO;
import com.damien.campusordering.dto.OrdersSubmitDTO;
import com.damien.campusordering.vo.OrderPaymentVO;
import com.damien.campusordering.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
}