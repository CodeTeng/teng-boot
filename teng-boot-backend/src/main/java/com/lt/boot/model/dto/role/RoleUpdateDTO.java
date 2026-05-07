package com.lt.boot.model.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Schema(description = "更新角色请求体")
public class RoleUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色权限字符串", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色权限字符串不能为空")
    private String roleKey;

    @Schema(description = "显示顺序")
    private Integer roleSort;

    @Schema(description = "状态 0-停用 1-正常")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
