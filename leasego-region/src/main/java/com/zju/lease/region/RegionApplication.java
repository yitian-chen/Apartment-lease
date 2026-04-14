package com.zju.lease.region;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.zju.lease.region.mapper")
public class RegionApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegionApplication.class, args);
    }
}
