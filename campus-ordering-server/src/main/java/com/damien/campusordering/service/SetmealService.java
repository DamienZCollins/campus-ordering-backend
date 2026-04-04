package com.damien.campusordering.service;

import com.damien.campusordering.dto.SetmealDTO;
import com.damien.campusordering.dto.SetmealPageQueryDTO;
import com.damien.campusordering.entity.Setmeal;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.vo.DishItemVO;
import com.damien.campusordering.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 根据分类id查询套餐
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    /**
     * 新增套餐（含套餐菜品关联）
     *
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据 id 查询套餐及关联菜品（回显）
     *
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐（含关联菜品）
     *
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
