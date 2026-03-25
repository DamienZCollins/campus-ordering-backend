package com.damien.campusordering.controller.admin;

import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

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
        return Result.success();
    }
}
