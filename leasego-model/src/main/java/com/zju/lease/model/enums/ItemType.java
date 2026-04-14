package com.zju.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ItemType implements BaseEnum {

    APARTMENT(1, "公寓"),

    ROOM(2, "房间");


    @EnumValue // 此注解可以允许 Mybatis Plus 的 TypeHandler 使 ItemType 对象实例与 code 相互映射
    @JsonValue // 此注解可以允许 SpringMVC 的 HTTPMessageConverter 使 ItemType 对象实例与 code 相互映射
    private Integer code;
    private String name;

    @Override
    public Integer getCode() {
        return this.code;
    }


    @Override
    public String getName() {
        return name;
    }

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
