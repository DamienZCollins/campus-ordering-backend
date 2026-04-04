package com.damien.campusordering.service.impl;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.dto.SetmealDTO;
import com.damien.campusordering.dto.SetmealPageQueryDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.entity.Setmeal;
import com.damien.campusordering.entity.SetmealDish;
import com.damien.campusordering.exception.BaseException;
import com.damien.campusordering.exception.DeletionNotAllowedException;
import com.damien.campusordering.exception.SetmealEnableFailedException;
import com.damien.campusordering.mapper.DishMapper;
import com.damien.campusordering.mapper.SetmealDishMapper;
import com.damien.campusordering.mapper.SetmealMapper;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.service.SetmealService;
import com.damien.campusordering.vo.DishItemVO;
import com.damien.campusordering.vo.SetmealVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        if (CollectionUtils.isEmpty(setmealDTO.getSetmealDishes())) {
            throw new BaseException(MessageConstant.SETMEAL_MUST_HAVE_DISH);
        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        if (setmeal.getStatus() == null) {
            setmeal.setStatus(StatusConstant.DISABLE);
        }

        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal == null) {
                throw new BaseException(MessageConstant.SETMEAL_NOT_FOUND);
            }
            if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        for (Long setmealId : ids) {
            setmealMapper.deleteById(setmealId);
            setmealDishMapper.deleteBySetmealId(setmealId);
        }
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        if (setmeal == null) {
            return null;
        }
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        if (CollectionUtils.isEmpty(setmealDTO.getSetmealDishes())) {
            throw new BaseException(MessageConstant.SETMEAL_MUST_HAVE_DISH);
        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        if (StatusConstant.ENABLE.equals(status)) {
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if (dishList != null) {
                for (Dish dish : dishList) {
                    if (StatusConstant.DISABLE.equals(dish.getStatus())) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
