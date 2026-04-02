package com.damien.campusordering.service;

import com.damien.campusordering.dto.UserLoginDTO;
import com.damien.campusordering.entity.User;

public interface UserService {
    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
