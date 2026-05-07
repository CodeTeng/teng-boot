package com.lt.boot.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Schema(description = "添加菜单请求体")
public class MenuAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "路由参数")
    private String query;

    @Schema(description = "菜单类型 M-目录 C-菜单 F-按钮")
    private String menuType;

    @Schema(description = "显示状态 0-隐藏 1-显示")
    private Integer visible;

    @Schema(description = "菜单状态 0-停用 1-正常")
    private Integer status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "备注")
    private String remark;
}
