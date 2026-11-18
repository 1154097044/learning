package com.xy.learning.week3mybatis.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/** 不把数据库 SQL、连接信息或堆栈直接暴露给调用方。参数错误由 Spring MVC 返回 400。 */
@RestController
public class ApiExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integrityConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "数据约束冲突：邮箱可能已存在，或关联用户不存在"));
    }
}
