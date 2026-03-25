package com.damien.campusordering.service;

import com.damien.campusordering.dto.DishDTO;

public interface DishService {
    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
}
