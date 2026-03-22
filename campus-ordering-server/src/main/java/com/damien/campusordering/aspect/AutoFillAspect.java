package com.damien.campusordering.aspect;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;
import com.damien.campusordering.annotation.AutoFill;
import com.damien.campusordering.constant.AutoFillConstant;
import com.damien.campusordering.context.BaseContext;
import com.damien.campusordering.enumeration.OperationType;

/**
 * 公共字段填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.damien.campusordering.mapper.*.*(..)) && @annotation(com.damien.campusordering.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 前置通知,在通知字段里赋值
     */
    @Before("autoFillPointcut()")
    public void beforeFill(JoinPoint joinPoint) {
        log.info("开始填充公共字段...");
        //获取当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        log.info("数据库操作类型: {}", operationType);
        //获取到当前被拦截的方法上的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        //准备要赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentID = BaseContext.getCurrentId();
        //根据当前不同的操作类型,为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
            // 为四个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射为对应的属性赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentID);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentID);
            } catch (Exception e) {
                log.error("公共字段自动填充失败，操作类型：INSERT，实体类：{}", entity.getClass().getName(), e);
                throw new RuntimeException("公共字段自动填充失败", e);
            }
        } else if (operationType == OperationType.UPDATE) {
            // 为两个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射为对应的属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentID);
            } catch (Exception e) {
                log.error("公共字段自动填充失败，操作类型：UPDATE，实体类：{}", entity.getClass().getName(), e);
                throw new RuntimeException("公共字段自动填充失败", e);
            }
        }

    }
    
    
    
}
