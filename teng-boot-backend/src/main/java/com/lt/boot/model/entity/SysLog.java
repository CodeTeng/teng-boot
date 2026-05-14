package com.lt.boot.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志表
 *
 * @TableName sys_log
 */
@TableName(value = "sys_log")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统日志实体")
public class SysLog implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "用户账号")
    private String username;
    @Schema(description = "用户操作描述")
    private String value;
    @Schema(description = "请求方式:GET POST PUT DELETE")
    private String operation;
    @Schema(description = "响应时间,单位毫秒")
    private Long costTime;
    @Schema(description = "请求url")
    private String url;
    @Schema(description = "请求方法（控制层方法全限定名）")
    private String methodName;
    @Schema(description = "请求参数")
    private String params;
    @Schema(description = "IP地址")
    private String ip;
    @Schema(description = "操作系统")
    private String os;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "是否删除 0-未删除 1-已删除")
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}