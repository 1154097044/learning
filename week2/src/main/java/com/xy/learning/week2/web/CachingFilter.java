package com.xy.learning.week2.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * 报文缓存过滤器：把 request/response 包装成可重复读的缓存版本，
 * 让 TraceInterceptor 在 afterCompletion 里能拿到请求体与响应体。
 *
 * 为什么必须在 Filter 层做包装：
 *   拦截器拿到的 request/response 是容器已经固定的对象，无法替换后续链上使用的 response；
 *   而 Filter 是链的最外层，包装之后传下去，DispatcherServlet 和拦截器拿到的都是包装后的对象。
 *
 * 两个坑：
 *   1. copyBodyToResponse() 必须在 finally 里调用，否则客户端收不到响应体；
 *   2. 不支持 multipart 文件上传与流式响应（SSE/大文件下载），那些场景不能把报文全量缓存在内存。
 */
@Component
public class CachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // 关键：拦截器打完日志后，把缓存的响应体写回真正的 response
            wrappedResponse.copyBodyToResponse();
        }
    }
}
