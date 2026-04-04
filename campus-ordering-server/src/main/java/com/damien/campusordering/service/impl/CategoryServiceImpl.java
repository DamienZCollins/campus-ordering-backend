package com.damien.campusordering.service.impl;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.convert.CategoryConvert;
import com.damien.campusordering.dto.CategoryDTO;
import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.entity.Category;
import com.damien.campusordering.exception.DeletionNotAllowedException;
import com.damien.campusordering.mapper.CategoryMapper;
import com.damien.campusordering.mapper.DishMapper;
import com.damien.campusordering.mapper.SetmealMapper;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryConvert categoryConvert;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    @Override
    @CacheEvict(cacheNames = {"dishCache", "setmealCache"}, allEntries = true)
    public void save(CategoryDTO categoryDTO) {
        Category category = categoryConvert.toEntity(categoryDTO);
        //分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insert(category);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    @CacheEvict(cacheNames = {"dishCache", "setmealCache"}, allEntries = true)
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = categoryConvert.toEntity(categoryDTO);
        categoryMapper.update(category);
    }

    /**
     * 分类启用禁用
     *
     * @param status
     * @param id
     */
    @CacheEvict(cacheNames = {"dishCache", "setmealCache"}, allEntries = true)
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    /**
     * 分类列表
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    /**
     * 分类删除
     *
     * @param id
     */
    @CacheEvict(cacheNames = {"dishCache", "setmealCache"}, allEntries = true)
    @Override
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteById(id);
    }



}
