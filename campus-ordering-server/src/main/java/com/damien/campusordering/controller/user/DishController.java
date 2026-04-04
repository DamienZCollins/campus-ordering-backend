package com.damien.campusordering.controller.user;

import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.DishService;
import com.damien.campusordering.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        return Result.success(dishService.listWithFlavor(dish));
    }
}
