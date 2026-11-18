package com.xy.learning.week3mybatis.dto;

import jakarta.validation.constraints.*;

import java.util.List;

/** 请求 DTO 与数据库 POJO 分开，客户端不能覆盖 id 或 createdAt。 */
public final class Request {

    private Request(){}

    public record UserRequest(
            @NotBlank @Size(max = 50) String name,
            @NotBlank @Email @Size(max = 100) String email){}

    public record TodoRequest(
        @NotNull @Positive Long userId,
        @NotBlank @Size(max = 100) String title,
        @Size(max = 500) String description,
        @NotNull Boolean done){}

    public record BatchDoneRequest(
            @NotEmpty @Size(max = 100) List<@NotNull @Positive Long> ids,
            @NotNull Boolean done){}

}
