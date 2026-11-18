package com.xy.learning.week3mybatis.dto;

import java.util.List;

/** 原生 MyBatis 没有 Page 对象，total 来自 COUNT，items 来自 LIMIT。page 从 1 开始。 */
public record PageResult<T>(List<T> items,long total,int page,int size) {
}
