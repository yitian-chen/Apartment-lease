package com.zju.lease.apartment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.zju.lease.apartment.mapper")
public class ApartmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApartmentApplication.class, args);
    }
}
