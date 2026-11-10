package com.xy.learning.week2.web;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

/**
 * traceId + 耗时拦截器。
 * <p>
 * 【Android 对照】你给 OkHttp 加 Interceptor 打请求日志、透传 EagleEye-TraceId，
 * 服务端的 HandlerInterceptor 就是同一思路的镜像：请求进来 preHandle、处理完 afterCompletion。
 * <p>
 * 做三件事：
 * 1. preHandle：取请求头 X-Trace-Id，没有就生成一个，放进 MDC（日志上下文）与响应头。
 * 2. preHandle：记录开始时间到 request attribute。
 * 3. afterCompletion：算耗时打日志，并清理 MDC（防止线程复用导致 traceId 串味）。
 * <p>
 * MDC 里的 traceId 会自动出现在日志里，因为 application.yml 的日志 pattern 配了 %X{traceId}。
 */

@Component
public class TraceInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TraceInterceptor.class);
    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final String ATTR_START = "reqStartTime";

    /**
     * 报文日志单段最大长度，超过则截断，防止大报文打爆日志
     */
    private static final int MAX_BODY_LEN = 2000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        MDC.put("traceId", traceId);
        response.setHeader(TRACE_HEADER, traceId);
        request.setAttribute(ATTR_START, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        try {
            Object start = request.getAttribute(ATTR_START);
            long cost = start == null ? -1 : System.currentTimeMillis() - (Long) start;
            log.info("{}", buildAccessLog(request, response, cost));
        } finally {
            MDC.remove("traceId");// 必须清理：Tomcat 线程是复用的
        }
    }

    /**
     * 组装完整访问日志：方法+URI、请求头、请求体、状态码、耗时、响应体
     */
    private JSONObject buildAccessLog(HttpServletRequest request, HttpServletResponse response, long cost) {
        JSONObject requestJson = new JSONObject();
        JSONObject responseJson = new JSONObject();
        requestJson.put("headers", headersOf(request));
        requestJson.put("method", request.getMethod());
        requestJson.put("url", String.format(Locale.CHINA, "%s://%s%s?%s",
                request.getScheme(),
                request.getRemoteHost(),
                request.getRequestURI(),
                request.getQueryString()));
        requestJson.put("cookie",request.getCookies());
        requestJson.put("authType",request.getAuthType());
        requestJson.put("status", response.getStatus());
        requestJson.put("duration", cost + "ms");
        // 请求体：只有被 CachingFilter 包装过、且 handler 读过流（如 @RequestBody）才有内容
        if (request instanceof ContentCachingRequestWrapper wrapped) {
            String body = new String(wrapped.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!body.isEmpty()) {
                requestJson.put("body", parseBody(body));
            }
        }

        responseJson.put("headers", headersOf(response));
        responseJson.put("status", response.getStatus());
        // 响应体：同样来自缓存（此时 copyBodyToResponse 还没执行，缓存里数据还在）
        if (response instanceof ContentCachingResponseWrapper wrapped) {
            String body = new String(wrapped.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!body.isEmpty()) {
                responseJson.put("body", parseBody(body));
            }
        }

        JSONObject result = new JSONObject();
        result.put("request", requestJson);
        result.put("response", responseJson);
        return result;
    }

    /**
     * 遍历请求头。生产上注意：Authorization/Cookie 等敏感头需要脱敏后再打日志
     */
    private JSONObject headersOf(HttpServletRequest request) {
        JSONObject header = new JSONObject();
        Collections.list(request.getHeaderNames()).forEach(name ->
                header.put(name, request.getHeader(name)));
        return header;
    }

    /**
     * 遍历请求头。生产上注意：Authorization/Cookie 等敏感头需要脱敏后再打日志
     */
    private JSONObject headersOf(HttpServletResponse response) {
        JSONObject header = new JSONObject();
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(name -> header.put(name, response.getHeader(name)));
        return header;
    }

    /**
     * 超过 MAX_BODY_LEN 截断
     */
    private String truncate(String body) {
        return body.length() <= MAX_BODY_LEN
                ? body
                : body.substring(0, MAX_BODY_LEN) + "...(truncated, total " + body.length() + " chars)";
    }

    /**
     * 合法且长度可控的 JSON 直接解析为对象，日志里就是结构化嵌套；
     * 非 JSON（text/plain、表单等）或超长报文则保留原文并截断。
     * 注意顺序：先判断长度再 isValid，因为截断后的 JSON 必然解析失败。
     */
    private Object parseBody(String body) {
        if (body.length() <= MAX_BODY_LEN && JSON.isValid(body)) {
            try {
                return JSON.parse(body);
            } catch (Exception e) {
                // 兜底：日志组件绝不能因为解析失败反噬主链路，更不能整条访问日志丢失
                return truncate(body);
            }
        }
        return truncate(body);
    }
}
