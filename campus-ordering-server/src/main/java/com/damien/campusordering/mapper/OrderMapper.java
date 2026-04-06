package com.damien.campusordering.mapper;

import com.damien.campusordering.dto.GoodsSalesDTO;
import com.damien.campusordering.dto.OrdersPageQueryDTO;
import com.damien.campusordering.entity.Orders;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

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

    /**
     * 根据时间范围和状态统计营业额（按天分组）
     *
     * @param begin  开始时间
     * @param end    结束时间
     * @param status 订单状态
     * @return 每天的营业额列表
     */
    @MapKey("date")
    List<Map<String, Object>> getTurnoverByDateRange(@Param("begin") LocalDateTime begin,
                                                     @Param("end") LocalDateTime end,
                                                     @Param("status") Integer status);

    /**
     * 根据时间范围统计订单数据（按天分组）
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天的订单统计列表
     */
    List<Map<String, Object>> getOrderStatisticsByDateRange(@Param("begin") LocalDateTime begin,
                                                            @Param("end") LocalDateTime end);

    /**
     * 根据时间范围统计销量前10的商品数据
     *
     * @param begin  开始时间
     * @param end    结束时间
     * @param status 订单状态
     * @return 销量前10的商品列表
     */
    List<GoodsSalesDTO> getSalesTop10ByDateRange(@Param("begin") LocalDateTime begin,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("status") Integer status);

    /**
     * 根据条件统计订单数量
     *
     * @param map 查询条件（可含 begin, end, status）
     * @return 订单数量
     */
    Integer countByMap(Map map);

    /**
     * 根据条件统计营业额
     *
     * @param map 查询条件（可含 begin, end, status）
     * @return 营业额
     */
    Double sumByMap(Map map);
}
