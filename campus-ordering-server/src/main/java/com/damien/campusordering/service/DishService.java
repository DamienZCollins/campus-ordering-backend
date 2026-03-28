package com.damien.campusordering.service;

import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和对应的口味
     *
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);
}
