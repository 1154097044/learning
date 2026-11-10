package com.xy.learning.week2.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建 Todo 的请求体。校验注解配合 Controller 方法参数上的 @Valid 生效。
 *
 * 注意包名是 jakarta.validation 而非 javax.validation —— Spring Boot 3 起迁移到 Jakarta EE 9+，
 * 这是从旧教程/旧项目迁移时最常见的编译错误（见主文档"常见坑排查表"）。
 */
public record TodoCreateRequest(

        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题不能超过100个字符")
        String title,

        @Size(max = 500, message = "描述不能超过500个字符")
        String description
) {

        @Override
        public String toString() {
                return "TodoCreateRequest{" +
                        "title='" + title + '\'' +
                        ", description='" + description + '\'' +
                        '}';
        }
}
