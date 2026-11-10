package com.xy.learning.week2.common;


/**
 * 业务异常。Service 层遇到"业务上不允许"的情况就抛它，由全局异常处理器统一转成 Result。
 * 继承 RuntimeException：这样 @Transactional 默认会回滚（见第 3 周）。
 */
public class BizException extends RuntimeException{

    private final ResultCode resultCode;

    public BizException(ResultCode resultCode,String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    /** 便捷方法：资源不存在 */
    public static BizException notFound(String message) {
        return new BizException(ResultCode.NOT_FOUND, message);
    }
}
