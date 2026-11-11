package com.xy.learning.week3tx.common;

/**
 * 统一响应体。所有 Controller 都返回 Result&lt;T&gt;，前端拿到结构一致的 {code,message,data}。
 * 用 record 写不可变数据载体（Java 17+），对照 Android 里手写的 Response&lt;T&gt; 包装类。
 *
 * 从 week2 原样移植，只多了一个 error(ResultCode) 便捷重载（message 直接取枚举里的默认文案）。
 */
public record Result<T>(String code, String message, T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static Result<Void> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message, null);
    }
}
