package com.damien.campusordering.convert;

import com.damien.campusordering.dto.EmployeeDTO;
import com.damien.campusordering.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConvert {

    EmployeeConvert INSTANCE = Mappers.getMapper(EmployeeConvert.class);

    Employee toEntity(EmployeeDTO employeeDTO);
}
