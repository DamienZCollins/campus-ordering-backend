package com.damien.campusordering.controller.admin;

import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.DishService;
import com.damien.campusordering.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result<Void> save(@RequestBody DishDTO dishDTO) {
        dishService.saveWithFlavor(dishDTO);
        log.info("新增菜品{}", dishDTO);
        // 清理缓存
        cleanCache(dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<Void> delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品{}", ids);
        dishService.deleteBatch(ids);
        // 清理所有缓存
        cleanAllCache();
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam("categoryId") Long categoryId) {
        log.info("根据分类id查询菜品{}", categoryId);
        return Result.success(dishService.list(categoryId));
    }

    /**
     * 根据id查询菜品和对应的口味
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品信息{}", id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result<Void> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        // 清理所有缓存
        cleanAllCache();
        return Result.success();
    }

    /**
     * 起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<Void> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("起售停售{}", id);
        dishService.startOrStop(status, id);
        // 清理所有缓存
        cleanAllCache();
        return Result.success();
    }

    /**
     * 清理指定分类的缓存
     *
     * @param categoryId 分类ID
     */
    private void cleanCache(Long categoryId) {
        redisTemplate.delete("dish_" + categoryId);
    }

    /**
     * 清理所有菜品缓存
     */
    private void cleanAllCache() {
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }

}
