package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     *
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品 id 批量删除口味
     *
     * @param ids
     */
    void deleteByDishIds(@Param("ids") List<Long> ids);
}
