package com.damien.campusordering.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class PasswordEditDTO implements Serializable {

    //旧密码
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 6, message = "密码至少6位")
    private String oldPassword;

    //新密码
    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, message = "密码至少6位")
    private String newPassword;

}

