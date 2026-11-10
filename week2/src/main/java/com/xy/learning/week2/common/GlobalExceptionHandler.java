package com.xy.learning.week2.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 全局异常处理。所有 Controller 抛出的异常都在这里被"兜住"并转成统一的 Result。
 * 好处：Controller 里不用写一堆 try-catch，业务代码干净；前端拿到的错误结构永远一致。
 *
 * 处理优先级（从具体到宽泛）：
 *   1. MethodArgumentNotValidException —— @RequestBody + @Valid 校验失败（哪个字段、什么原因）
 *   2. BizException                     —— 业务主动抛出的异常（如资源不存在）
 *   3. Exception                        —— 兜底，避免把堆栈直接暴露给客户端
 *
 * 对照 Android：相当于 OkHttp 的统一错误拦截 + Retrofit 的 onResponse 错误分支，集中处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 参数校验失败：把每个字段的错误原因收集成 map 返回，而不是甩一个 500 堆栈 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String,String>> handleValidation(MethodArgumentNotValidException ex){
        Map<String,String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "非法值" : fe.getDefaultMessage(),
                        (a,b) -> a,
                        LinkedHashMap::new));
        log.warn("参数校验失败：{}",fieldErrors);
        return Result.error(ResultCode.BAD_REQUEST.getCode(),"参数校验失败: " + fieldErrors);
    }

    /** 业务异常：用异常自带的业务码，HTTP 状态按语义映射（不存在→404） */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException ex){
        log.warn("业务异常：code={},msg={}",ex.getResultCode().getCode(),ex.getMessage());
        return Result.error(ex.getResultCode(), ex.getMessage());
    }

    /** 兜底异常：记录完整堆栈到日志，但只给客户端返回模糊信息，防止内部细节泄露 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnknown(Exception ex){
        log.error("未预期的服务器异常",ex);
        return Result.error(ResultCode.INTERNAL_ERROR,ResultCode.INTERNAL_ERROR.getMessage());
    }
}
