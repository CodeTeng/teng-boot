package com.lt.boot.model.dto.role;

import com.lt.boot.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "角色分页查询请求体")
public class RoleQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符串")
    private String roleKey;

    @Schema(description = "状态 0-停用 1-正常")
    private Integer status;
}
