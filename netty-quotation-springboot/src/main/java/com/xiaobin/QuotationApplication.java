package com.xiaobin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 21:28
 * @desc springboot启动类
 */
@SpringBootApplication
@EnableScheduling
public class QuotationApplication{

    public static void main(String[] args) {
        SpringApplication.run(QuotationApplication.class);
    }

}
