package com.damien.campusordering.service.impl;

import com.damien.campusordering.service.ShopService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 营业状态缓存服务实现。
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Override
    @Cacheable(cacheNames = "shopCache", key = "'status'")
    public Integer getStatus() {
        return null;
    }

    @Override
    @CachePut(cacheNames = "shopCache", key = "'status'")
    public Integer setStatus(Integer status) {
        return status;
    }
}
