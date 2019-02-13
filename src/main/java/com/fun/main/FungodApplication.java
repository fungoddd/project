package com.fun.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableTransactionManagement //如果mybatis中service实现类中加入事务注解，需要此处添加该注解
@ComponentScan(basePackages = {"com.fun"})
@MapperScan("com.fun.dao")
public class FungodApplication {
    public static void main(String[] args) {
        SpringApplication.run(FungodApplication.class, args);
    }
}
