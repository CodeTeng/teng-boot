package com.lt.boot.common;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回类
 */
@Data
@Schema(description = "通用响应结果")
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {
    @Schema(description = "业务状态码，200-成功，其它-失败")
    private int code;
    @Schema(description = "响应消息", example = "OK")
    private T data;
    @Schema(description = "响应数据")
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode, T data) {
        this(errorCode.getCode(), data, errorCode.getMessage());
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
