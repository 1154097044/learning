package com.xy.learning.week3mybatis.service;

import com.xy.learning.week3mybatis.dto.PageResult;
import com.xy.learning.week3mybatis.dto.Request;
import com.xy.learning.week3mybatis.mapper.TodoMapper;
import com.xy.learning.week3mybatis.mapper.UserMapper;
import com.xy.learning.week3mybatis.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller -> Service -> Mapper -> XML SQL。
 * 无自动脏检查、无 save 合并语义：INSERT/UPDATE/DELETE 各自显式调用。
 */
@Service
public class TodoService {

    private final TodoMapper todoMapper;
    private final UserMapper userMapper;

    public TodoService(TodoMapper todoMapper, UserMapper userMapper) {
        this.todoMapper = todoMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public Todo create(Request.TodoRequest request){
        requireUser(request.userId());
        Todo todo = new Todo();
        apply(todo,request);
        todo.setCreatedAt(LocalDateTime.now());
        todoMapper.insert(todo);
        return get(todo.getId());
    }

    public Todo get(Long id){
        Todo todo = todoMapper.findById(id);
        if (todo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo 不存在：" + id);
        }
        return todo;
    }

    @Transactional
    public Todo update(Long id, Request.TodoRequest request){
        Todo todo = get(id);
        requireUser(request.userId());
        apply(todo,request);
        // 改Java 对象不会自动落库； XML 的 UPDATE 不更新 created_at。
        if (todoMapper.update(todo) != 1){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo 已被删除：" + id);
        }
        return get(id);
    }

    @Transactional
    public void delete(Long id){
        if (todoMapper.deleteById(id) != 1){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo 不存在：" + id);
        }
    }

    /** COUNT 与 LIMIT 放同一只读事务；MySQL 默认 REPEATABLE READ 下读同一快照。 */
    @Transactional(readOnly = true)
    public PageResult<Todo> list(Long userId,Boolean done,String keyword,int page,int size){
        if (page < 1 || size < 1 || size > 100){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 1，size 在 1~100 之间");
        }
        long offset = ((long) page - 1) * size; // 先转long,防止int乘法溢出
        long total = todoMapper.count(userId,done,keyword);
        List<Todo> items = todoMapper.findPage(userId,done,keyword,offset,size);
        return new PageResult<>(items,total,page,size);
    }

    @Transactional
    public int updateDoneBatch(List<Long> ids, boolean done) {
        if (ids == null || ids.isEmpty() || ids.size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ids 数量须为 1~100");
        }
        return todoMapper.updateDoneBatch(ids, done);
    }

    private void requireUser(Long userId){
        if (userMapper.findById(userId) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在：" + userId);
        }
    }

    private void apply(Todo todo, Request.TodoRequest request){
        todo.setUserId(request.userId());
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setDone(request.done());
    }
}
