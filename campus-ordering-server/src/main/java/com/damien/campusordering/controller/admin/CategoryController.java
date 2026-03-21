package com.damien.campusordering.controller.admin;

import com.damien.campusordering.dto.CategoryDTO;
import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        return Result.success(categoryService.pageQuery(categoryPageQueryDTO));
    }


    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result<Void> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping
    public Result<Void> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用/禁用分类
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<Void> startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用/禁用分类，分类 ID:{}, 状态:{}", id, status);
        categoryService.startOrStop(status, id);
        return Result.success();
    }
}

