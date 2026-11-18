package com.xy.learning.week3mybatis.web;

import com.xy.learning.week3mybatis.dto.PageResult;
import com.xy.learning.week3mybatis.dto.Request;
import com.xy.learning.week3mybatis.dto.TxResult;
import com.xy.learning.week3mybatis.dto.UserTodoCount;
import com.xy.learning.week3mybatis.model.Todo;
import com.xy.learning.week3mybatis.model.User;
import com.xy.learning.week3mybatis.service.RegistrationDemoService;
import com.xy.learning.week3mybatis.service.TodoService;
import com.xy.learning.week3mybatis.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mybatis")
public class MyBatisController {

    private final TodoService todoService;
    private final UserService userService;
    private final RegistrationDemoService registrationDemo;

    public MyBatisController(TodoService todoService, UserService userService, RegistrationDemoService registrationDemo) {
        this.todoService = todoService;
        this.userService = userService;
        this.registrationDemo = registrationDemo;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody Request.UserRequest request){
        return userService.create(request);
    }

    @GetMapping("/users/with-todos")
    public List<User> usersWithTodos(){
        return userService.withTodos();
    }

    @GetMapping("/users/n-plus-one")
    public List<User> nPlusOne() {
        return userService.nPlusOne();
    }

    @GetMapping("/users/count")
    public List<UserTodoCount> userCounts() {
        return userService.counts();
    }

    @PostMapping("/todos")
    public Todo createTodo(@Valid @RequestBody Request.TodoRequest request) {
        return todoService.create(request);
    }

    @GetMapping("/todos/{id}")
    public Todo getTodo(@Positive @PathVariable Long id) {
        return todoService.get(id);
    }

    @PutMapping("/todos/{id}")
    public Todo updateTodo(@Positive @PathVariable Long id, @Valid @RequestBody Request.TodoRequest request) {
        return todoService.update(id, request);
    }

    @DeleteMapping("/todos/{id}")
    public Map<String, String> deleteTodo(@Positive @PathVariable Long id) {
        todoService.delete(id);
        return Map.of("message", "已删除");
    }

    @GetMapping("/todos")
    public PageResult<Todo> list(@RequestParam(required = false) Long userId,
                                 @RequestParam(required = false) Boolean done,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return todoService.list(userId, done, keyword, page, size);
    }

    @PutMapping("/todos/batch-done")
    public Map<String, Integer> updateDoneBatch(@Valid @RequestBody Request.BatchDoneRequest request) {
        return Map.of("affectedRows", todoService.updateDoneBatch(request.ids(), request.done()));
    }

    @PostMapping("/tx/{scenario}")
    public TxResult transaction(@PathVariable String scenario, @Valid @RequestBody Request.UserRequest request) {
        return registrationDemo.run(scenario, request);
    }
}
