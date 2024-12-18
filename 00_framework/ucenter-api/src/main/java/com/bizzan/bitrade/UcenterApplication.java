package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Jammy
 * @date 2020年02月06日
 * <p>
 * exclude = {MongoAutoConfiguration.class,MongoDataAutoConfiguration.class}
 * 禁用springboot自带的mongodb配置（localhost），不禁用虽然也可以，但每次启动都会出现报错信息
 */

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class UcenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcenterApplication.class, args);
    }
}
