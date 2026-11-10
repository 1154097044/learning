package com.xy.learning.week3.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 用户实体：把 Java 类映射到 MySQL 的 app_user 表。
 *
 * 【Android 对照】对标 Room 的 @Entity(tableName=...) + @PrimaryKey(autoGenerate=true)。
 * 关键差异：JPA 实体必须是可变 POJO，且必须有 protected 无参构造器（Hibernate
 * 用反射创建对象再 setter 填值），Room 没这个硬要求。
 */
@Entity
@Table(name = "app_user") // 表名；user 是 MySQL 保留字，所以用 app_user
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL AUTO_INCREMENT，插入后 id 自动回填到实体
    private Long id;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false,length = 100,unique = true)
    private String email;

    /** 创建时间：updatable = false 表示"更新操作不碰这一列" */
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    /** JPA 必须有 protected 无参构造器（Jackson 反序列化也会用到它） */
    protected User(){}

    /** 便捷构造器：入门演示用，自动填创建时间 */
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