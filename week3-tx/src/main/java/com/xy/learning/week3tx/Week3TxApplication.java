package com.xy.learning.week3tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 第 3 周事务 Demo 启动类。
 *
 * 运行前提：Docker MySQL 已启动 + learn_tx 库已创建（建库命令见 application.yml 注释）。
 * 本模块不需要 @MapperScan（用的是 JPA，不是 MyBatis）。
 */
@SpringBootApplication
public class Week3TxApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week3TxApplication.class,args);
    }
}
