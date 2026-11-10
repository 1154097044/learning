package com.xy.learning.week2.todo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xy.learning.week2.common.BizException;
import com.xy.learning.week2.common.PageResult;
import com.xy.learning.week2.todo.dto.TodoCreateRequest;
import com.xy.learning.week2.todo.dto.TodoUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo create(TodoCreateRequest request){
        log.info("create todo: " + request);
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDone(false);
        return todoRepository.save(todo);
    }

    public PageResult<Todo> list(int page,int size){
        log.info("page:" + page + ", size:" + size);
        Page<Todo> pages = todoRepository.findPage(page,size);
        return new PageResult<>(pages.getRecords(),pages.getTotal(),pages.getPages(),pages.getSize());
    }

    public Todo get(Long id){
        return todoRepository.findById(id)
                .orElseThrow(() -> BizException.notFound("Todo 不存在：id = " + id));
    }

    public Todo update(Long id, TodoUpdateRequest request) {
        log.info("update todo: " + id + " " + request);
        Todo todo = get(id); // 复用查询，不存在会抛 404 业务异常
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDone(request.done());
        // 更新必须走 update(updateById)；若误用 save(insert) 会变成"再插一条新记录"，原记录不变
        return todoRepository.update(todo);
    }

    public void delete(Long id) {
        log.info("delete todo: " + id);
        if (todoRepository.findById(id).isEmpty()) {
            throw BizException.notFound("Todo 不存在，无法删除: id=" + id);
        }
        todoRepository.deleteById(id);
    }
}
