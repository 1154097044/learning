package com.xy.learning.week3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 第 2 周启动类：Web 开发核心。
 *
 * 本模块演示一个"待办事项(Todo)"的完整 REST API（内存存储版，第 3 周会替换为数据库）：
 *   POST   /api/todos          创建（校验：标题非空、长度≤100）
 *   GET    /api/todos          分页列表
 *   GET    /api/todos/{id}     详情（不存在返回 404 语义业务码）
 *   PUT    /api/todos/{id}     更新
 *   DELETE /api/todos/{id}     删除
 *
 * 四个贯穿全局的工程能力（都是 Android 客户端里没有、服务端必须掌握的）：
 *   1. 统一响应体 Result<T>            —— common/Result.java
 *   2. 全局异常处理 @RestControllerAdvice —— common/GlobalExceptionHandler.java
 *   3. 参数校验 @Valid + 校验注解        —— todo/dto/*.java
 *   4. traceId + 耗时拦截器              —— web/TraceInterceptor.java（对标 OkHttp Interceptor）
 *
 * 运行：mvn -pl week2-web spring-boot:run
 * 默认端口 8082，可用 --server.port 覆盖。
 */
@SpringBootApplication
public class Week3WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week3WebApplication.class);
    }
}
