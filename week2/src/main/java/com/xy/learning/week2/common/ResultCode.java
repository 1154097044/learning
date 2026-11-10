package com.xy.learning.week2.common;

/**
 * 统一业务码。code 用字符串便于扩展，也可换成 int。
 * 约定：0 成功；4xxxx 客户端错误；5xxxx 服务端错误。
 */
public enum ResultCode {

    SUCCESS("0", "成功"),
    BAD_REQUEST("40000", "请求参数错误"),
    NOT_FOUND("40400", "资源不存在"),
    INTERNAL_ERROR("50000", "服务器内部错误");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
