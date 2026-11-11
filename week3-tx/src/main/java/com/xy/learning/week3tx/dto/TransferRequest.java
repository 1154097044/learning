package com.xy.learning.week3tx.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 转账请求体。四个场景接口（/ok、/rollback、/self-bug、/self-fixed）共用。
 * 校验注解配合 Controller 方法参数上的 @Valid 生效，失败会抛 MethodArgumentNotValidException，
 * 由 GlobalExceptionHandler 统一转成 HTTP 400 + 字段级错误明细。
 *
 * <p>约束取值都对齐了 Account 实体与 V1__init.sql 的表结构：
 * <ul>
 *   <li>name VARCHAR(50) NOT NULL → @Size(max = 50)</li>
 *   <li>balance DECIMAL(10,2)     → @Digits(integer = 8, fraction = 2)（10 位总长 - 2 位小数）</li>
 * </ul>
 * 把库表约束前移到入参校验，能在进事务之前就拦掉必然失败的请求，
 * 否则要等 JDBC 抛 data truncation 才知道，白白开了一次事务。
 *
 * <p>注意包名是 jakarta.validation 而非 javax.validation —— Spring Boot 3 起迁移到 Jakarta EE 9+，
 * 这是从旧教程/旧项目迁移时最常见的编译错误。
 */
public record TransferRequest(

        @NotBlank(message = "转出账户不能为空")
        @Size(max = 50, message = "转出账户名不能超过50个字符")
        String from,

        @NotBlank(message = "转入账户不能为空")
        @Size(max = 50, message = "转入账户名不能超过50个字符")
        String to,

        @NotNull(message = "转账金额不能为空")
        @Positive(message = "转账金额必须为正数")
        @Digits(integer = 8, fraction = 2, message = "转账金额最多8位整数、2位小数")
        BigDecimal amount
) {
}
