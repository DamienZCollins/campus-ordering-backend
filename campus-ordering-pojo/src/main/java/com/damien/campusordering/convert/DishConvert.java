package com.damien.campusordering.convert;

import com.damien.campusordering.dto.DishDTO;
import com.damien.campusordering.entity.Dish;
import com.damien.campusordering.vo.DishVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DishConvert {
    Dish toEntity(DishDTO dishDTO);

    DishVO toVO(Dish dish);
}
