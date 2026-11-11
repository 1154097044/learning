package com.xy.learning.week3tx;

import com.xy.learning.week3tx.entity.Account;
import com.xy.learning.week3tx.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 启动后初始化两个演示账户：Alice=100, Bob=50（表空时才插）。
 */
@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final AccountRepository repo;

    public DataInitRunner(AccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() == 0) {
            repo.save(new Account("Alice", new BigDecimal("100.00")));
            repo.save(new Account("Bob", new BigDecimal("50.00")));
            log.info(">>> 表是空的，已初始化 Alice=100, Bob=50");
        }
        repo.findAll().forEach(a ->
                log.info(">>> 账户: {} = {}", a.getName(), a.getBalance()));
    }
}
