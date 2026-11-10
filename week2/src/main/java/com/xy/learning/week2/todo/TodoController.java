package com.xy.learning.week2.todo;

import com.xy.learning.week2.common.PageResult;
import com.xy.learning.week2.common.Result;
import com.xy.learning.week2.todo.dto.TodoCreateRequest;
import com.xy.learning.week2.todo.dto.TodoUpdateRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private static Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public Result<Todo> create(@Valid @RequestBody TodoCreateRequest request){
        logger.info("create todo: {}", request.toString());
        return Result.success(todoService.create(request));
    }

    @GetMapping
    public Result<PageResult<Todo>> list(
            // MP 的 Page 页码从 1 开始（JPA Pageable 从 0 开始），默认值用 1 更规范
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        return Result.success(todoService.list(page,size));
    }

    @GetMapping("/get")
    public Result<Todo> get(@RequestParam(defaultValue = "0") Long id){
        return Result.success(todoService.get(id));
    }

    @PutMapping("/update")
    public Result<Todo> update(@RequestParam Long id, @Valid @RequestBody TodoUpdateRequest request) {
        return Result.success(todoService.update(id, request));
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        todoService.delete(id);
        return Result.success();
    }
}
