package com.xy.learning.week3mybatis.service;

import com.xy.learning.week3mybatis.dto.Request;
import com.xy.learning.week3mybatis.dto.TxResult;
import com.xy.learning.week3mybatis.mapper.TodoMapper;
import com.xy.learning.week3mybatis.mapper.UserMapper;
import com.xy.learning.week3mybatis.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.DecapsulateException;

/**
 * 无事务的演示入口：事务退出后再查库，观察真正的提交/回滚结果。
 * 修复自调用的推荐方式是拆 Bean，通过注入的 UserService 代理调用事务方法，
 * 不需要自注入、@Lazy 或放开循环依赖。
 * 这是故意包含错误场景的学习接口，不用于生产注册/鉴权。
 */
@Service
public class RegistrationDemoService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final TodoMapper todoMapper;

    public RegistrationDemoService(UserService userService, UserMapper userMapper, TodoMapper todoMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.todoMapper = todoMapper;
    }

    /** 每次实验使用不同邮箱；self-bug 会真的留下数据，重复邮箱应返回 409。 */
    public TxResult run(String scenario, Request.UserRequest request){
        try{
            switch (scenario){
                case "ok" -> userService.createWithFirstTodo(request,false);
                case "rollback" -> userService.createWithFirstTodo(request,true);
                case "self-bug" -> userService.selfInvocationBug(request);
                // 从本 Bean 调另一个 Bean 的 public 事务方法，就是拆 Bean 修复。
                case "self-fixed" -> userService.createWithFirstTodo(request, true);
                default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未知演示场景");
            }
        }catch (UserService.DemoFailureException excepted){
            // 异常已经经过事务代理：若事务生效，这时回滚已完成。SQL 错误不在此吞掉。
        }
        User user = userMapper.findByEmail(request.email());
        long count = user == null ? 0 : todoMapper.count(user.getId(),null,null);
        return new TxResult(scenario,user != null,count);
    }
}
