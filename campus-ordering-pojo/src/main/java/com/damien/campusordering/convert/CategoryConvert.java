package com.damien.campusordering.convert;

import com.damien.campusordering.dto.CategoryDTO;
import com.damien.campusordering.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryConvert {

    Category toEntity(CategoryDTO categoryDTO);
}
