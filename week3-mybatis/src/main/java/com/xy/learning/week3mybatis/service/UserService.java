package com.xy.learning.week3mybatis.service;

import com.xy.learning.week3mybatis.dto.PageResult;
import com.xy.learning.week3mybatis.dto.Request;
import com.xy.learning.week3mybatis.dto.UserTodoCount;
import com.xy.learning.week3mybatis.mapper.TodoMapper;
import com.xy.learning.week3mybatis.mapper.UserMapper;
import com.xy.learning.week3mybatis.model.Todo;
import com.xy.learning.week3mybatis.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserMapper userMapper;
    private final TodoMapper todoMapper;

    public UserService(UserMapper userMapper, TodoMapper todoMapper) {
        this.userMapper = userMapper;
        this.todoMapper = todoMapper;
    }

    @Transactional
    public User create(Request.UserRequest request){
        User user = newUser(request);
        userMapper.insert(user);
        return user;
    }

    /** 全部集合在一次 JOIN 内组装好，不依赖 JPA 的 OSIV 或懒加载 Session。 */
    public List<User> withTodos(){
        return userMapper.findAllWithTodos();
    }

    /** MyBatis 也能写出 N+1：一次查用户，然后每个用户再查待办。本方法仅用于对照。 */
    @Transactional(readOnly = true)
    public List<User> nPlusOne(){
        List<User> users = userMapper.findAll();
        for (User user : users) {
            user.setTodos(todoMapper.findByUserId(user.getId()));
        }
        return users;
    }

    public List<UserTodoCount> counts(){
        return userMapper.countTodosPerUser();
    }

    /**
     * 两表工作流：创建用户，再创建第一条待办。
     * 经 Spring 代理调用时，两次 Mapper 写入共享同一事务。
     * failAfterUser=true 故意中途抛 RuntimeException，默认规则会回滚已经插入的用户。
     * Mapper 本身没有 @Transactional 也能加入 Service 开启的事务。
     */
    @Transactional
    public User createWithFirstTodo(Request.UserRequest request,boolean failAfterUser){
        log.info("工作流中是否存在事务：{}",
                TransactionSynchronizationManager.isActualTransactionActive());
        User user = newUser(request);
        userMapper.insert(user);
        if (failAfterUser){
            throw new DemoFailureException();
        }
        Todo todo = new Todo();
        todo.setUserId(user.getId());
        todo.setTitle("新用户第一条待办");
        todo.setDescription("MyBatis 不级联保存，第二张表也要明确调用 insert");
        todo.setDone(false);
        todo.setCreatedAt(LocalDateTime.now());
        todoMapper.insert(todo);
        return user;
    }

    /**
     * 故意错误：本入口没有事务，this 调用绕过代理，被调方法的注解不会开启事务。
     * MyBatis-Spring 在事务外会提交 Mapper 写操作，因此用户会留下，待办没创建。
     * 别给这个入口或演示测试加事务，否则外层事务会掩盖此问题。
     */
    public void selfInvocationBug(Request.UserRequest request){
        this.createWithFirstTodo(request,true);
    }

    private User newUser(Request.UserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        return user;
    }

    /** 只捕获这种故意抛出的异常，不能把真实 SQL 错误也包装为“回滚成功”。 */
    public static class DemoFailureException extends RuntimeException {
        public DemoFailureException() {
            super("模拟：用户已插入，创建待办前出错");
        }
    }
}
