package com.xy.learning.week2.common;

/**
 *
 * 统一响应体。所有 Controller 都返回 Result<T>，前端拿到结构一致的 {code,message,data}。
 * 用 record 写不可变数据载体（Java 17+），对照 Android 里你手写的 Response<T> 包装类。
 */
public record Result<T> (String code,String message,T data){

    public static <T> Result<T> success(T data){
        return new Result<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    public static Result<Void> success(){
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message, null);
    }
}
