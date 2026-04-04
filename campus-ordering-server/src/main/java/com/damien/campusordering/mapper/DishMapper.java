package com.damien.campusordering.mapper;

import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.enumeration.OperationType;
import com.damien.campusordering.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
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
     * 批量查询菜品状态
     *
     * @param ids 菜品ID列表
     * @return 菜品列表
     */
    List<Dish> listByIds(List<Long> ids);

    /**
     * 动态条件查询菜品
     *
     * @param dish 查询条件（可含 categoryId、status、name）
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐 id 查询关联菜品（用于套餐起售校验）
     *
     * @param setmealId 套餐 id
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 修改菜品
     *
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
}
