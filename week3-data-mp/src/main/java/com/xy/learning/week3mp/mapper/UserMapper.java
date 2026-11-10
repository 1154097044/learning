package com.xy.learning.week3mp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.learning.week3mp.entity.User;

/**
 * 用户 Mapper：继承 BaseMapper<User> 白拿单表 CRUD。
 *
 * 【与 JPA 版对照】JPA 的 JpaRepository 是按"方法名派生"生成查询；
 * MP 的 BaseMapper 只给固定方法（insert/deleteById/updateById/selectById/
 * selectList/selectPage...），条件查询靠调用方传 LambdaQueryWrapper，
 * 复杂 SQL 再写 XML。两种"自动生成实现"的哲学完全不同。
 *
 * 单表增删改查这里什么都不用写；联表/复杂查询才需要写方法 + XML 映射文件。
 */
public interface UserMapper extends BaseMapper<User> {
}
