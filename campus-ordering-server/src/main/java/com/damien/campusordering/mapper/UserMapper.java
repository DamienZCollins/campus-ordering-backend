package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据时间范围统计用户数据（按天分组）
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天的新增用户数和累计用户数
     */
    @MapKey("date")
    List<Map<String, Object>> getUserStatisticsByDateRange(@Param("begin") LocalDateTime begin,
                                                           @Param("end") LocalDateTime end);
}