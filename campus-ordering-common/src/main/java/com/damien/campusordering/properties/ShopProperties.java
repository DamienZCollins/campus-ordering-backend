package com.damien.campusordering.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "campus.ordering")
@Data
public class ShopProperties {

    /**
     * 店铺地址
     */
    private String shopAddress;

    /**
     * 百度地图 AK
     */
    private String baiduAk;

}
