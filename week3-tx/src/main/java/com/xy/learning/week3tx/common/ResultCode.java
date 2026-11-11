package com.xy.learning.week3tx.common;

import org.springframework.http.HttpStatus;

/**
 * 统一业务码。code 用字符串便于扩展，也可换成 int。
 * 约定：0 成功；4xxxx 客户端错误；5xxxx 服务端错误。
 *
 * 与 week2 版本的差别：这里多带了一个 {@link HttpStatus}。
 * week2 的 GlobalExceptionHandler 处理 BizException 时没有设 HTTP 状态码，
 * 所以"资源不存在"虽然业务码是 40400，HTTP 层返回的仍然是 200。
 * 本模块要求"账户不存在返回 404"，因此把业务码到 HTTP 状态的映射固化在枚举里，
 * 由 GlobalExceptionHandler 用 ResponseEntity 显式写出。
 */
public enum ResultCode {

    SUCCESS("0", "成功", HttpStatus.OK),
    BAD_REQUEST("40000", "请求参数错误", HttpStatus.BAD_REQUEST),
    NOT_FOUND("40400", "资源不存在", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("40500", "请求方法不支持", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE("41500", "Content-Type 不支持", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INTERNAL_ERROR("50000", "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ResultCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
