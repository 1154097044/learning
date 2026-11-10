package com.xy.learning.week3mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户实体：MyBatis-Plus 版。
 *
 * 【与 JPA 版对照】同一张表、同样的字段，注解完全不同：
 *
 * | 职责 | JPA 版（week3-data） | MP 版（本文件） |
 * |---|---|---|
 * | 类↔表映射 | @Entity + @Table(name="app_user") | @TableName("app_user") |
 * | 主键自增 | @Id + @GeneratedValue(IDENTITY) | @TableId(type = IdType.AUTO) |
 * | 列约束 | @Column(nullable=false, length=50) | 不写（MP 不管约束，约束在 DDL 里） |
 *
 * MP 的实体是"纯 POJO + 极少量注解"：字段默认驼峰转下划线映射到列
 * （userId ↔ user_id），不需要 @Column 逐个声明。
 */
@TableName("app_user")
public class User {

    // 数据库自增主键，insert 后 id 自动回填
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String email;

    private LocalDateTime createdAt;

    /** MP 不强制无参构造器，但 Jackson 反序列化需要，保留 */
    public User(){}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
