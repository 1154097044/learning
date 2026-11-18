package com.xy.learning.week3mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xy.learning.week3mybatis.mapper")
public class Week3MybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week3MybatisApplication.class,args);
    }
}
