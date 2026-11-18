package com.xy.learning.week3mybatis.dto;

/** 联表统计 DTO：普通 POJO，XML 将聚合列 todo_count 显式映射到 todoCount。 */
public class UserTodoCount {

    private Long userId;
    private String name;
    private long todoCount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(long todoCount) {
        this.todoCount = todoCount;
    }
}
