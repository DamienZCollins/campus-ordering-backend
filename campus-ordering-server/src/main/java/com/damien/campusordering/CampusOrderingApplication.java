package com.damien.campusordering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching //开启注解方式的缓存管理
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
public class CampusOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusOrderingApplication.class, args);
        log.info("server started");
    }
}

