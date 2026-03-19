package com.damien.campusordering.controller.admin;

import com.damien.campusordering.constant.JwtClaimsConstant;
import com.damien.campusordering.dto.EmployeeDTO;
import com.damien.campusordering.dto.EmployeeLoginDTO;
import com.damien.campusordering.dto.EmployeePageQueryDTO;
import com.damien.campusordering.dto.PasswordEditDTO;
import com.damien.campusordering.entity.Employee;
import com.damien.campusordering.properties.JwtProperties;
import com.damien.campusordering.result.PageResult;
import com.damien.campusordering.result.Result;
import com.damien.campusordering.service.EmployeeService;
import com.damien.campusordering.utils.JwtUtil;
import com.damien.campusordering.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @return
     */
    @PostMapping
    public Result<Void> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工，员工数据：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 封装分页查询结果
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询：{}", employeePageQueryDTO);
        return Result.success(employeeService.pageQuery(employeePageQueryDTO));
    }

    /**
     * 启用/禁用员工账号
     *
     * @param status
     * @param id
     */
    @PostMapping("/status/{status}")
    public Result<Void> startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用/禁用员工账号，员工 ID:{}, 状态:{}", id, status);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     *
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息,{}", id);
        return Result.success(employeeService.getById(id));
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     * @return
     */
    @PutMapping()
    public Result<Void> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息,{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     * @return
     */
    @PutMapping("/editPassword")
    public Result<Void> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("修改密码");
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}

