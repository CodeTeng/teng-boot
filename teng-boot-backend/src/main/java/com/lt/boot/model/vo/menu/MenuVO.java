package com.lt.boot.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "菜单视图")
public class MenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
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

    @Schema(description = "子节点")
    private List<MenuVO> children;
}
