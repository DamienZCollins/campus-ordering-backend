package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{id}")
    User getById(Long id);
}