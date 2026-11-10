package com.xy.learning.week2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.learning.week2.todo.Todo;

/**
 * Todo Mapper：继承 BaseMapper<Todo> 白拿单表 CRUD（insert/updateById/selectById/
 * selectList/selectPage/deleteById...），条件查询靠调用方传 LambdaQueryWrapper。
 * 被 Week2WebApplication 的 @MapperScan 扫描注册为 Bean。
 */
public interface TodoMapper extends BaseMapper<Todo> {
}
