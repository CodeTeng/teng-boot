package com.lt.boot.model.dto.log;

import com.lt.boot.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/19 20:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "后台日志分页查询请求体")
public class SysLogQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "主键id")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "用户账号")
    private String username;
    @Schema(description = "用户操作描述")
    private String value;
    @Schema(description = "用户操作:DELETE ADD GET UPDATE")
    private String operation;
    @Schema(description = "请求url")
    private String url;
    @Schema(description = "请求方法（控制层方法全限定名）")
    private String methodName;
    @Schema(description = "IP地址")
    private String ip;
}
