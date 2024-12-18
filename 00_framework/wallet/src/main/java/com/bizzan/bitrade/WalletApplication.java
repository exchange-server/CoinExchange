package com.bizzan.bitrade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WalletApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
