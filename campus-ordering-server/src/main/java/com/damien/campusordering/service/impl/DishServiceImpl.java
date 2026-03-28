package com.damien.campusordering.service.impl;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.convert.DishConvert;
import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.entity.DishFlavor;
import com.damien.campusordering.exception.DeletionNotAllowedException;
import com.damien.campusordering.mapper.DishFlavorMapper;
import com.damien.campusordering.mapper.DishMapper;
import com.damien.campusordering.mapper.SetmealDishMapper;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.service.DishService;
import com.damien.campusordering.vo.DishVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishConvert dishConvert;
    @Autowired
    private DishFlavorMapper dishFlavorsMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("新增菜品{}", dishDTO);
        Dish dish = dishConvert.toEntity(dishDTO);
        //插入菜品数据
        dishMapper.insert(dish);
        //插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //给口味数据设置菜品id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            //批量插入
            dishFlavorsMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断是否起售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            //起售中的套餐
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
        dishMapper.deleteBatch(ids);
        //删除菜品口味数据
        dishFlavorsMapper.deleteByDishIds(ids);
    }

    /**
     * 根据分类id查询菜品分类
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.list(categoryId);
    }


    /**
     * 根据id查询菜品和对应的口味数据
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        log.info("根据 id 查询菜品和口味{}", id);
        // 查询菜品
        Dish dish = dishMapper.getById(id);
        // 查询口味
        List<DishFlavor> flavors = dishFlavorsMapper.getByDishId(id);
        // 组装 VO
        DishVO dishVO = dishConvert.toVO(dish);
        if (flavors != null) {
            dishVO.setFlavors(flavors);
        }
        return dishVO;
    }
}
