package com.damien.campusordering.service.impl;

import com.damien.campusordering.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    private static final Integer DEFAULT_STATUS = 0;

    @Override
    @Cacheable(cacheNames = "shopCache", key = "'status'", unless = "#result == null")
    public Integer getStatus() {
        log.warn("Redis缓存未命中且无数据库回源，返回默认营业状态");
        return DEFAULT_STATUS;
    }

    @Override
    @CachePut(cacheNames = "shopCache", key = "'status'")
    public Integer setStatus(Integer status) {
        return status;
    }
}
