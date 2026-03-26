package com.damien.campusordering.service.impl;

import com.damien.campusordering.convert.DishConvert;
import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.dto.DishPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.entity.DishFlavor;
import com.damien.campusordering.mapper.DishFlavorMapper;
import com.damien.campusordering.mapper.DishMapper;
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
}
