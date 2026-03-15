package com.damien.campusordering.service.impl;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.constant.PasswordConstant;
import com.damien.campusordering.constant.StatusConstant;
import com.damien.campusordering.context.BaseContext;
import com.damien.campusordering.dto.EmployeeDTO;
import com.damien.campusordering.dto.EmployeeLoginDTO;
import com.damien.campusordering.entity.Employee;
import com.damien.campusordering.exception.AccountLockedException;
import com.damien.campusordering.exception.AccountNotFoundException;
import com.damien.campusordering.exception.PasswordErrorException;
import com.damien.campusordering.mapper.EmployeeMapper;
import com.damien.campusordering.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

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
        // md5转换
        //TODO 可以优化为BCrypt
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
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
        Employee employee = new Employee();
        //TODO 可以优化为MapStruct
        BeanUtils.copyProperties(employeeDTO, employee);
        //TODO 可以使用AOP
        //默认账号状态
        employee.setStatus(StatusConstant.ENABLE);
        //默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //创建人、修改人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

}

