package com.damien.campusordering.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.dto.UserLoginDTO;
import com.damien.campusordering.entity.User;
import com.damien.campusordering.exception.LoginFailedException;
import com.damien.campusordering.mapper.UserMapper;
import com.damien.campusordering.properties.WeChatProperties;
import com.damien.campusordering.service.UserService;
import com.damien.campusordering.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信登录接口
    public static final String WE_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties wechatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    @Transactional
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口,获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        //如果是新用户
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户信息
        return user;
    }

    /**
     * 调用微信接口,获取openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        //调用微信接口,获取openid
        Map<String, String> map = new HashMap<>();

        map.put("js_code", code);
        map.put("appid", wechatProperties.getAppid());
        map.put("secret", wechatProperties.getSecret());
        map.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WE_LOGIN, map);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
