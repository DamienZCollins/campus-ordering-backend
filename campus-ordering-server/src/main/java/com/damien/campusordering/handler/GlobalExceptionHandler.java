package com.damien.campusordering.handler;

import com.damien.campusordering.constant.MessageConstant;
import com.damien.campusordering.exception.BaseException;
import com.damien.campusordering.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQL异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result l(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            Pattern pattern = Pattern.compile("Duplicate entry '(.+)' for key '(.+)'");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String duplicateValue = matcher.group(1);
                String keyName = matcher.group(2);
                if (keyName != null && keyName.contains("username")) {
                    return Result.error(MessageConstant.USERNAME_ALREADY_EXISTS);
                }
                return Result.error(duplicateValue + MessageConstant.ALREADY_EXISTS);
            }
        }
        log.error("SQL异常", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 捕获参数校验异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError != null) {
            return Result.error(fieldError.getDefaultMessage());
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 捕获未知异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(Exception ex) {
        log.error("系统异常", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}

