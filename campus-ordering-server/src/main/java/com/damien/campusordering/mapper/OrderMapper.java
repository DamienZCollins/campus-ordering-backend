package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     *
     * @param orders
     * @return
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单状态和下单时间查询订单
     *
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 批量更新订单状态
     *
     * @param status
     * @param cancelReason
     * @param cancelTime
     * @param ids
     */
    void updateBatchByStatusAndIds(@Param("status") Integer status,
                                   @Param("cancelReason") String cancelReason,
                                   @Param("cancelTime") LocalDateTime cancelTime,
                                   @Param("ids") List<Long> ids);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO
     */
    com.github.pagehelper.Page<Orders> pageQuery(com.damien.campusordering.dto.OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}