package com.lt.boot.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "role")
@Data
@Schema(description = "角色实体")
public class Role implements Serializable {
    @TableId(type = IdType.AUTO)
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
    @Schema(description = "创建者id")
    @TableField(fill = FieldFill.INSERT)
    private Long creater;
    @Schema(description = "更新者id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @Schema(description = "是否删除")
    @TableLogic
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
