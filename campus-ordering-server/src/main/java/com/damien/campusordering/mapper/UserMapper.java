package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 新增用户
     *
     * @param user
     */
    void insert(User user);
}
