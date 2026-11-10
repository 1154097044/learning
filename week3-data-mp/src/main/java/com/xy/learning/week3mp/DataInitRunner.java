package com.xy.learning.week3mp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xy.learning.week3mp.entity.User;
import com.xy.learning.week3mp.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final UserMapper userMapper;

    public DataInitRunner(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(null) == 0) {
            userMapper.insert(new User("张三", "zhangsan@example.com"));
            userMapper.insert(new User("李四", "lisi@example.com"));
            userMapper.insert(new User("王五", "wangwu@example.com"));
            log.info(">>> 表是空的，已自动插入 3 条演示数据");
        }
        log.info(">>> app_user 表当前总条数: {}", userMapper.selectCount(null));

        userMapper.selectList(new LambdaQueryWrapper<User>()
                        .like(User::getName, "张"))
                .forEach(u -> log.info(">>> 模糊查询到用户: id={}, name={}, email={}",
                        u.getId(), u.getName(), u.getEmail()));
    }
}
