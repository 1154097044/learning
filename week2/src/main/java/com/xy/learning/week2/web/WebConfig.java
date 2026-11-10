package com.xy.learning.week2.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器。实现 WebMvcConfigurer 覆盖 addInterceptors，把 TraceInterceptor 挂到 /api/** 路径。
 * 对照 Android：类似在 OkHttp 客户端 builder 上 addInterceptor。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TraceInterceptor traceInterceptor;

    public WebConfig(TraceInterceptor traceInterceptor) {
        this.traceInterceptor = traceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceInterceptor).addPathPatterns("/api/**");
    }
}
