package com.xy.learning.week3rel;

import com.xy.learning.week3rel.entity.Todo;
import com.xy.learning.week3rel.entity.User;
import com.xy.learning.week3rel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动后初始化演示数据（表空时）：Alice 带 2 条待办、Bob 带 1 条、Carol 0 条。
 * Carol 故意留空，用来验证联表计数里 left join 能把她以 0 计数带出来。
 */
@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final UserRepository userRepo;

    public DataInitRunner(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            User alice = new User("Alice");
            alice.addTodo(new Todo("学习 JPA 一对多"));
            alice.addTodo(new Todo("跑通联表计数"));
            userRepo.save(alice);   // cascade=ALL → 两条 todo 级联落库

            User bob = new User("Bob");
            bob.addTodo(new Todo("复现 N+1"));
            userRepo.save(bob);

            userRepo.save(new User("Carol"));   // 0 条待办，验证 left join
            log.info(">>> 表是空的，已初始化 Alice(2 todo)/Bob(1 todo)/Carol(0 todo)");
        }
        userRepo.findAll().forEach(u ->
                log.info(">>> 用户: {}", u.getName()));
    }
}
