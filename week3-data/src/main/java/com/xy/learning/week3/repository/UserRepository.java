package com.xy.learning.week3.repository;

import com.xy.learning.week3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口：只写接口不写实现，Spring 运行时动态生成实现类并注册成 Bean。
 *
 * 【Android 对照】对标 Room 的 @Dao 接口——但更省事：继承 JpaRepository
 * 就有 save/findById/findAll/deleteById/count 全套 CRUD，零 SQL、零实现。
 *
 * 方法名派生查询：Spring 解析方法名自动生成 SQL，规则速查见
 * 《MySQL接入SpringBoot实战手册》3.2 节。
 */
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * 按邮箱精确查询（登录场景），Optional 表示"可能不存在"
     */
    Optional<User> findByEmail(String email);

    /**
     * 按姓名模糊查询：SELECT * FROM app_user WHERE name LIKE '%keyword%'
     */
    List<User> findByNameContaining(String keyword);

    /**
     * 统计姓名包含关键词的用户数：SELECT count(*) FROM app_user WHERE ...
     */
    long countByNameContaining(String keyword);

    /**
     * 查询创建时间晚于某时刻的用户：WHERE created_at > ?
     */
    List<User> findByCreatedAtAfter(LocalDateTime time);
}
