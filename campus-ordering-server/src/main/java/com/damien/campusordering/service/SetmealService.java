package com.damien.campusordering.service;

import com.damien.campusordering.entity.Setmeal;
import com.damien.campusordering.vo.DishItemVO;

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
}
