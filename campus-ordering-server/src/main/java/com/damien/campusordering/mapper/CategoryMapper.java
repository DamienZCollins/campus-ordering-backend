package com.damien.campusordering.mapper;

import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.dto.CategoryPageQueryDTO;
import com.damien.campusordering.entity.Category;
import com.damien.campusordering.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     *
     * @param category
     */
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
     @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    /**
     * 修改分类
     *
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    /**
     * 根据类型查询
     *
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 根据id删除
     *
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
