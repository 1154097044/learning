package com.xy.learning.week2.todo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xy.learning.week2.mapper.TodoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Todo 数据访问层：包装 MyBatis-Plus 的 TodoMapper（数据库版，非内存版）。
 *
 * 【JPA → MyBatis 迁移说明】本类原本是内存版（ConcurrentHashMap + AtomicLong），
 * 现已改为注入 TodoMapper 走真实 MySQL。保留 Repository 这层是为了让 Service
 * 不直接依赖 MP 的 Wrapper 细节（仓储模式）。
 *
 * 【迁移最易踩的坑】JPA 的 save() 是 merge 语义：实体有 id 就 UPDATE、无 id 就 INSERT，
 * 自动区分；MP 没有这种智能方法——insert() 永远 INSERT、updateById() 才 UPDATE。
 * 所以这里必须把 save（新建）和 update（更新）拆成两个方法，不能像 JPA 那样一个 save 走天下。
 */
@Repository
public class TodoRepository {

    private static final Logger log = LoggerFactory.getLogger(TodoRepository.class);
    private final TodoMapper todoMapper;

    public TodoRepository(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    /** 新建：insert，id 由数据库自增回填。仅用于无 id 的新实体 */
    public Todo save(Todo todo){
        todoMapper.insert(todo);
        return todo;
    }

    /** 更新：updateById，按 id 更新非 null 字段。务必与 save 区分，勿混用 */
    public Todo update(Todo todo){
        todoMapper.updateById(todo);
        return todo;
    }


    public Optional<Todo> findById(Long id){
        return Optional.ofNullable(todoMapper.selectById(id));
    }

    /** 简单分页：按 id 升序返回指定页的数据 */
    public Page<Todo> findPage(int page,int size){
        return todoMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Todo>().orderByAsc(Todo::getId));
    }


    public void deleteById(Long id){
        todoMapper.deleteById(id);
    }
}
