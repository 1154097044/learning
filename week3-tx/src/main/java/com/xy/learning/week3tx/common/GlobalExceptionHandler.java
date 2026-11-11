package com.xy.learning.week3tx.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理。所有 Controller 抛出的异常都在这里被"兜住"并转成统一的 Result。
 * 好处：Controller 里不用写一堆 try-catch，业务代码干净；前端拿到的错误结构永远一致。
 *
 * <p>从 week2 移植，但补了两处关键差异：
 * <ol>
 *   <li><b>HTTP 状态码真的按语义返回</b>。week2 的 handleBiz 没有设状态码，
 *       "资源不存在"业务码是 40400 但 HTTP 仍是 200。这里统一用 ResponseEntity 显式写出，
 *       状态码取自 {@link ResultCode#getHttpStatus()}。</li>
 *   <li><b>显式接管 Spring MVC 的标准异常</b>。原因见下面 handleUnknown 的注释——
 *       一旦写了 Exception 兜底，这些原本由容器映射成 4xx 的异常会被抢先变成 500。</li>
 * </ol>
 *
 * <p>对照 Android：相当于 OkHttp 的统一错误拦截 + Retrofit 的 onResponse 错误分支，集中处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 返回给客户端的异常详情最大长度，避免把 Jackson 的长篇内部描述整段甩出去 */
    private static final int MAX_DETAIL_LEN = 200;

    /**
     * @Valid 校验失败（@RequestBody 场景）：把每个字段的错误原因收集成 map 放进 data，
     * 客户端能直接定位是哪个字段不合法，而不是只看到一句"参数错误"。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "非法值" : fe.getDefaultMessage(),
                        (a, b) -> a,
                        LinkedHashMap::new));
        log.warn("参数校验失败：{}", fieldErrors);

        Result<Map<String, String>> body = new Result<>(
                ResultCode.BAD_REQUEST.getCode(), "参数校验失败", fieldErrors);
        return ResponseEntity.status(ResultCode.BAD_REQUEST.getHttpStatus()).body(body);
    }

    /**
     * 业务异常：HTTP 状态按 ResultCode 里固化的映射返回（不存在→404，参数错→400）。
     * 这是"账户不存在返回 404 而不是 500"的落点。
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBiz(BizException ex) {
        ResultCode rc = ex.getResultCode();
        log.warn("业务异常：code={},msg={}", rc.getCode(), ex.getMessage());
        return ResponseEntity.status(rc.getHttpStatus()).body(Result.error(rc, ex.getMessage()));
    }

    /**
     * 请求体读不出来：JSON 语法错误、body 缺失、Content-Type 与目标类型不匹配等。
     * 本轮调试最初那个 {"from":"Alice","to"="Bob"} 的 400 就落在这里——
     * 没有这个 handler 时客户端只能看到一句光秃秃的 "Bad Request"。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.warn("请求体解析失败：{}", ex.getMessage());
        return ResponseEntity.status(ResultCode.BAD_REQUEST.getHttpStatus())
                .body(Result.error(ResultCode.BAD_REQUEST, "请求体解析失败: " + truncate(ex.getMessage())));
    }

    /**
     * 缺少必填的 query / form 参数。
     * 例如 GET /api/tx/find 忘了带 ?name=xxx。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("缺少请求参数：{}", ex.getParameterName());
        return ResponseEntity.status(ResultCode.BAD_REQUEST.getHttpStatus())
                .body(Result.error(ResultCode.BAD_REQUEST,
                        "缺少必填参数: " + ex.getParameterName() + " (" + ex.getParameterType() + ")"));
    }

    /** 参数类型转换失败，例如 amount=abc 转不成 BigDecimal。 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String required = ex.getRequiredType() == null ? "未知类型" : ex.getRequiredType().getSimpleName();
        log.warn("参数类型不匹配：{}={} 无法转为 {}", ex.getName(), ex.getValue(), required);
        return ResponseEntity.status(ResultCode.BAD_REQUEST.getHttpStatus())
                .body(Result.error(ResultCode.BAD_REQUEST,
                        "参数类型不匹配: " + ex.getName() + "=" + ex.getValue() + " 无法转为 " + required));
    }

    /** 方法不支持，例如用 GET 调只开了 POST 的 /api/tx/ok。 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("请求方法不支持：{}", ex.getMessage());
        return ResponseEntity.status(ResultCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(Result.error(ResultCode.METHOD_NOT_ALLOWED,
                        "不支持 " + ex.getMethod() + "，允许的方法: " + ex.getSupportedHttpMethods()));
    }

    /**
     * Content-Type 不支持。典型场景：POST 发 form-urlencoded 但接口签名是 @RequestBody POJO——
     * FormHttpMessageConverter 只能读成 MultiValueMap，读不了自定义对象，于是 415。
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("Content-Type 不支持：{}", ex.getMessage());
        return ResponseEntity.status(ResultCode.UNSUPPORTED_MEDIA_TYPE.getHttpStatus())
                .body(Result.error(ResultCode.UNSUPPORTED_MEDIA_TYPE, truncate(ex.getMessage())));
    }

    /**
     * 兜底异常：记录完整堆栈到日志，但只给客户端返回模糊信息，防止内部细节泄露。
     *
     * <p>注意这里有个容易忽略的顺序问题：DispatcherServlet 里三个异常解析器的次序是
     * ExceptionHandlerExceptionResolver(0) → ResponseStatusExceptionResolver(1) →
     * DefaultHandlerExceptionResolver(2)。写了 Exception 兜底后，order=0 的它会抢在
     * DefaultHandlerExceptionResolver 之前命中，把原本该是 400/405/415 的异常统统变成 500。
     * 所以上面那些标准 MVC 异常必须逐个显式声明，不能只靠兜底。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnknown(Exception ex) {
        log.error("未预期的服务器异常", ex);
        return ResponseEntity.status(ResultCode.INTERNAL_ERROR.getHttpStatus())
                .body(Result.error(ResultCode.INTERNAL_ERROR));
    }

    /** 截断过长的异常描述，保留换行前的首段 */
    private static String truncate(String msg) {
        if (msg == null) {
            return "无详细信息";
        }
        String oneLine = msg.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= MAX_DETAIL_LEN ? oneLine : oneLine.substring(0, MAX_DETAIL_LEN) + "...";
    }
}
