package com.damien.campusordering.service;

/**
 * 营业状态缓存服务。
 */
public interface ShopService {

    /**
     * 获取营业状态
     *
     * @return 营业状态
     */
    Integer getStatus();

    /**
     * 设置营业状态
     *
     * @param status 营业状态
     */
    Integer setStatus(Integer status);
}
