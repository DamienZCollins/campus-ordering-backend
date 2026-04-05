package com.damien.campusordering.service.impl;

import com.damien.campusordering.context.BaseContext;
import com.damien.campusordering.dto.ShoppingCartDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.entity.Setmeal;
import com.damien.campusordering.entity.ShoppingCart;
import com.damien.campusordering.exception.ShoppingCartBusinessException;
import com.damien.campusordering.mapper.DishMapper;
import com.damien.campusordering.mapper.SetmealMapper;
import com.damien.campusordering.mapper.ShoppingCartMapper;
import com.damien.campusordering.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前用户,补充到购物车数据中
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .build();
        // 判断当前菜品或套餐是否在购物车中
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list != null && !list.isEmpty()) {
            // 当前菜品或套餐在购物车中, 数量+1
            ShoppingCart existedCart = list.get(0);
            existedCart.setNumber(existedCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(existedCart);
        } else {
            // 当前菜品或套餐不在购物车中,添加到购物车
            // 判断当前是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();

            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (setmealId != null) {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                if (setmeal == null) {
                    throw new ShoppingCartBusinessException("套餐不存在");
                }
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    @Transactional
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中某个商品
     *
     * @param shoppingCartDTO
     */
    @Override
    @Transactional
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //获取用户 构造要删的数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .build();
        // 查询
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 判断当前菜品或套餐是否在购物车中
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            // 如果购物车中数量为1,则删除
            if (cart.getNumber() == 1) {
                shoppingCartMapper.deleteById(cart.getId());
            } else {
                // 数量减1
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(cart);
            }
        }
    }
}

