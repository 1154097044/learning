package com.xy.learning.week3tx.common;

/**
 * 业务异常。Service 层遇到"业务上不允许"的情况就抛它，由全局异常处理器统一转成 Result。
 *
 * 继承 RuntimeException 在本模块尤其重要：@Transactional 默认只对 RuntimeException/Error 回滚，
 * 所以 debit() 里抛 BizException（账户不存在）时，同一事务中已执行的 SQL 会被正确回滚。
 * 如果改成继承 checked Exception，事务默认就不回滚了，得显式写 rollbackFor。
 */
public class BizException extends RuntimeException {

    private final ResultCode resultCode;

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    /** 便捷方法：资源不存在 → 业务码 40400 / HTTP 404 */
    public static BizException notFound(String message) {
        return new BizException(ResultCode.NOT_FOUND, message);
    }

    /** 便捷方法：参数错误 → 业务码 40000 / HTTP 400 */
    public static BizException badRequest(String message) {
        return new BizException(ResultCode.BAD_REQUEST, message);
    }
}
