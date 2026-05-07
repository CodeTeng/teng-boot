package com.lt.boot.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@TableName(value = "user_role")
@Data
@Schema(description = "用户角色关联实体")
public class UserRole implements Serializable {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "角色ID")
    private Long roleId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
