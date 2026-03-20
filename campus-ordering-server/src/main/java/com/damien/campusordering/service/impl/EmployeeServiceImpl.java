package com.damien.campusordering.service.impl;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.constant.PasswordConstant;
import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.context.BaseContext;
import com.damien.campusordering.dto.EmployeeDTO;
import com.damien.campusordering.dto.EmployeeLoginDTO;
import com.damien.campusordering.dto.EmployeePageQueryDTO;
import com.damien.campusordering.dto.PasswordEditDTO;
import com.damien.campusordering.entity.Employee;
import com.damien.campusordering.exception.AccountLockedException;
import com.damien.campusordering.exception.AccountNotFoundException;
import com.damien.campusordering.exception.BaseException;
import com.damien.campusordering.exception.PasswordErrorException;
import com.damien.campusordering.mapper.EmployeeMapper;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.service.EmployeeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeMapper.getByUsername(employeeDTO.getUsername());
        if (existingEmployee != null) {
            throw new BaseException(MessageConstant.USERNAME_ALREADY_EXISTS);
        }

        Employee employee = new Employee();
        //TODO 可以优化为MapStruct1
        BeanUtils.copyProperties(employeeDTO, employee);
        //TODO 可以使用AOP
        //默认账号状态
        employee.setStatus(StatusConstant.ENABLE);
        //默认密码
        employee.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));
        //创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //创建人、修改人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //TODO 可以优化为MapStruct2
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Long empId = BaseContext.getCurrentId();

        // 1. 根据当前登录员工id查询员工信息
        Employee employee = employeeMapper.getById(empId);
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 校验旧密码
        if (!passwordEncoder.matches(passwordEditDTO.getOldPassword(), employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 3.更新
        Employee updateEmployee = Employee.builder()
                .id(empId)
                .password(passwordEncoder.encode(passwordEditDTO.getNewPassword()))
                .updateTime(LocalDateTime.now())
                .updateUser(empId)
                .build();
        employeeMapper.update(updateEmployee);
    }
}

