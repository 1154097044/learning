package com.xy.learning.week2.todo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Todo 领域模型：MyBatis-Plus 版，映射到 MySQL 的 app_todo 表。
 * 用可变类而非 record，因为更新操作需要改字段。
 *
 * 【注解对照】@TableName("app_todo") ↔ JPA 的 @Entity+@Table；
 * @TableId(type=IdType.AUTO) ↔ JPA 的 @Id+@GeneratedValue(IDENTITY)。
 */
@TableName("app_todo")
public class Todo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private boolean done;

    /**
     * 创建时间。未写 @TableField("created_at")，靠 application.yml 的
     * map-underscore-to-camel-case=true 做驼峰↔下划线映射；删掉该配置会报"未知列 createdAt"。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    public Todo() {
    }

    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
        this.done = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
