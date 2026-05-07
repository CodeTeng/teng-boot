package com.lt.boot.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@TableName(value = "role_menu")
@Data
@Schema(description = "角色菜单关联实体")
public class RoleMenu implements Serializable {
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "菜单ID")
    private Long menuId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
