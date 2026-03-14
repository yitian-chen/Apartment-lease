package com.zju.lease.common.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.zju.lease.web.*.mapper")
public class MybatisPlusConfiguration {
}
