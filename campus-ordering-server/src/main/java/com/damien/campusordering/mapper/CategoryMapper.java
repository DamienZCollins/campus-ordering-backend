package com.damien.campusordering.mapper;

import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.entity.Category;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
