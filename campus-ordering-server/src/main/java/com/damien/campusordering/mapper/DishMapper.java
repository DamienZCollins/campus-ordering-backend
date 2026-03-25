package com.damien.campusordering.mapper;

import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

}