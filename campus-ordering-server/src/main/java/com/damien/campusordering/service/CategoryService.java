package com.damien.campusordering.service;

import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.result.PageResult;

public interface CategoryService {
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
