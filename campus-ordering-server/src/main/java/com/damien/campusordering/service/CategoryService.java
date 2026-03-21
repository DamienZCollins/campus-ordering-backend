package com.damien.campusordering.service;

import com.damien.campusordering.dto.CategoryDTO;
import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.entity.Category;
import com.damien.campusordering.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用/禁用分类
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 分类列表
     *
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 删除分类
     *
     * @param id
     */
    void deleteById(Long id);
}
