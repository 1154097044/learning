package com.xy.learning.week3mybatis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 纯 POJO：数据库约束在 Flyway，字段映射在 XML，没有 JPA/Plus 注解。
 * todos 是普通集合，不是 Hibernate 代理；MyBatis 不会自动级联保存它。
 * 只保留 User -> Todo 的单向返回结构，避免双向引用造成 JSON 无限递归。
 */
public class User {

    private Long id;
    private String name;
    private String email;
    private List<Todo> todos = new ArrayList<>();

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

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}
