package com.xy.learning.week3rel.web;

import com.xy.learning.week3rel.dto.UserTodoCountDto;
import com.xy.learning.week3rel.dto.UserWithTodosDto;
import com.xy.learning.week3rel.entity.Todo;
import com.xy.learning.week3rel.entity.User;
import com.xy.learning.week3rel.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/**
 *
 * 观察重点（开着日志看 SQL 条数）：
 *  - /users/n-plus-one       → 1 + N 条 select（N+1 灾难）
 *  - /users/n-plus-one-fixed → 1 条 join（EntityGraph 修复）
 *  - /users/{id}/lazy-bug    → 事务外碰懒加载集合，复现 LazyInitializationException
 */
@RestController
@RequestMapping("/api/rel")
public class RelController {

    private final UserService userService;

    public RelController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(@RequestParam String name) {
        return userService.createUser(name);
    }

    @PostMapping("/users/{id}/todos")
    public Todo addTodo(@PathVariable Long id, @RequestParam String title) {
        return userService.addTodo(id, title);
    }

    /** 正确姿势：事务内组装 DTO 返回（含 todos 标题） */
    @GetMapping("/users/{id}")
    public UserWithTodosDto getUser(@PathVariable Long id) {
        return userService.getUserWithTodos(id);
    }

    /** 联表计数：每个用户 + todo 数量（left join + group by，一条 SQL） */
    @GetMapping("/users/count")
    public List<UserTodoCountDto> count() {
        return userService.countTodosPerUser();
    }

    /** N+1 复现：看日志 1+N 条 select */
    @GetMapping("/users/n-plus-one")
    public Map<String, Object> nPlusOne() {
        return Map.of("hint", "N+1：去看日志，select 条数 = 1 + 用户数",
                "lines", userService.nPlusOne());
    }

    /** N+1 修复：看日志只有 1 条 join */
    @GetMapping("/users/n-plus-one-fixed")
    public Map<String, Object> nPlusOneFixed() {
        return Map.of("hint", "已修复：去看日志，只有 1 条 join select",
                "lines", userService.nPlusOneFixed());
    }

    /** 懒加载陷阱：事务外访问 getTodos() → LazyInitializationException */
    @GetMapping("/users/{id}/lazy-bug")
    public Map<String, Object> lazyBug(@PathVariable Long id) {
        return Map.of("result", userService.lazyBug(id));
    }
}
