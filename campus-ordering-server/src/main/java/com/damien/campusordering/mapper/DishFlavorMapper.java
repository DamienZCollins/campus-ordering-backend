package com.damien.campusordering.mapper;

import com.damien.campusordering.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     *
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
