package com.damien.campusordering.mapper;

import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.dto.EmployeePageQueryDTO;
import com.damien.campusordering.entity.Employee;
import com.damien.campusordering.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * 
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    /**
     * 新增员工
     *
     * @param employee
     */
    @Insert("insert into employee " +
            "(username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) "
            +
            "values" +
            "(#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Employee employee);




    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键id动态修改员工信息
     *
     * @param employee
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    Employee getById(Long id);
}
