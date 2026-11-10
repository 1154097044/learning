package com.xy.learning.week3mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xy.learning.week3mp.mapper")
public class Week3DataMpApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week3DataMpApplication.class,args);
    }
}
