package com.damien.campusordering.mapper;

import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.enumeration.OperationType;
import com.damien.campusordering.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查菜品
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} order by create_time desc")
    List<Dish> list(@Param("categoryId") Long categoryId);

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
