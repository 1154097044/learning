package com.xy.learning.week3;

import com.xy.learning.week3.entity.User;
import com.xy.learning.week3.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final UserRepository userRepository;

    public DataInitRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User("张三", "zhangsan@example.com"));
            userRepository.save(new User("李四", "lisi@example.com"));
            userRepository.save(new User("王五", "wangwu@example.com"));
            log.info(">>> 表是空的，已自动插入 3 条演示数据");
        }
        log.info(">>> app_user 表当前总条数: {}", userRepository.count());

        // 演示方法名派生查询：姓名包含"张"的用户（对应 Controller 的 /api/users/search）
        userRepository.findByNameContaining("张")
                .forEach(u -> log.info(">>> 模糊查询到用户: id={}, name={}, email={}",
                        u.getId(), u.getName(), u.getEmail()));
    }
}
