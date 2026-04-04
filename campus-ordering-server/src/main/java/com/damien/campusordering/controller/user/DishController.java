package com.damien.campusordering.controller.user;

import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.DishService;
import com.damien.campusordering.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    public static final String KEY_PREFIX = "dish_";

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        // 构造 Redis 的key
        String key = KEY_PREFIX + categoryId;
        //查询 Redis 中是否存在缓存菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //如果存在，直接返回，不用查询数据库
        if (list != null) {
            return Result.success(list);
        }

        //如果不存在，根据分类id查询菜品数据，并且缓存到 Redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        list = dishService.listWithFlavor(dish);

        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
