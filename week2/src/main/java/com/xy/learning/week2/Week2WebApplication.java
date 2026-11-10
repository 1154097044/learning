package com.xy.learning.week2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xy.learning.week2.mapper")
public class Week2WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week2WebApplication.class,args);
    }
}
