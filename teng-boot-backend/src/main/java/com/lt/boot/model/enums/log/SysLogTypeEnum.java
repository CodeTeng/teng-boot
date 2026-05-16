package com.lt.boot.model.enums.log;

import lombok.Getter;

@Getter
public enum SysLogTypeEnum {
    OPERATION(1, "操作日志"),
    LOGIN(2, "登录日志"),
    LOGOUT(3, "退出日志");

    private final Integer code;
    private final String desc;

    SysLogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}