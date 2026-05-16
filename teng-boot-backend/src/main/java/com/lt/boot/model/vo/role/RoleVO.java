package com.lt.boot.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "角色视图")
public class RoleVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符串")
    private String roleKey;

    @Schema(description = "显示顺序")
    private Integer roleSort;

    @Schema(description = "状态 0-停用 1-正常")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者ID")
    private Long creater;

    @Schema(description = "更新者ID")
    private Long updater;

    @Schema(description = "创建人用户名")
    private String creatorName;

    @Schema(description = "更新人用户名")
    private String updaterName;
}
