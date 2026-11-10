package com.xy.learning.week2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xy.learning.week2.mapper.TodoMapper;
import com.xy.learning.week2.todo.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final TodoMapper todoMapper;

    public DataInitRunner(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (todoMapper.selectCount(null) == 0) {
            // 演示数据用 Todo 语义（标题/描述），不要复用 week3 的 User 姓名/邮箱，避免数据串味
            todoMapper.insert(new Todo("学习 Spring Boot 第 2 周", "跑通 Todo REST API"));
            todoMapper.insert(new Todo("接入 MySQL", "用 MyBatis-Plus 替换内存存储"));
            todoMapper.insert(new Todo("Android 联调", "OkHttp 请求本机服务端"));
            log.info(">>> 表是空的，已自动插入 3 条演示数据");
        }
        log.info(">>> app_todo 表当前总条数: {}", todoMapper.selectCount(null));

        todoMapper.selectList(new LambdaQueryWrapper<Todo>()
                        .like(Todo::getTitle, "学习"))
                .forEach(u -> log.info(">>> 模糊查询到待办: id={}, title={}, content={}",
                        u.getId(), u.getTitle(), u.getDescription()));
    }
}
