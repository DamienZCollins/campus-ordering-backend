package com.damien.campusordering.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Employee login response")
public class EmployeeLoginVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("濮撳悕")
    private String name;

    @ApiModelProperty("jwt浠ょ墝")
    private String token;

}

