package com.damien.campusordering.service;

import com.damien.campusordering.dto.EmployeeDTO;
import com.damien.campusordering.dto.EmployeeLoginDTO;
import com.damien.campusordering.dto.EmployeePageQueryDTO;
import com.damien.campusordering.entity.Employee;
import com.damien.campusordering.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}

