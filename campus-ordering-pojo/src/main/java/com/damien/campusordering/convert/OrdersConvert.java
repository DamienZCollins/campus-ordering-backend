package com.damien.campusordering.convert;


import com.damien.campusordering.dto.OrdersSubmitDTO;
import com.damien.campusordering.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdersConvert {
    Orders toEntity(OrdersSubmitDTO orderSubmitDTO);

    OrdersSubmitDTO toDTO(Orders orders);
}
