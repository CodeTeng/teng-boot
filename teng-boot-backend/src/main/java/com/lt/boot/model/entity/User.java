package com.lt.boot.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import com.lt.boot.common.UserConvert;
import com.lt.boot.model.enums.user.UserGenderEnum;
import com.lt.boot.model.enums.user.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
@Schema(description = "用户实体")
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    @ExcelProperty("用户id")
    private Long id;
    @Schema(description = "用户账号")
    @ExcelProperty("用户账号")
    private String username;
    @Schema(description = "用户密码")
    @ExcelIgnore
    private String userPassword;
    @Schema(description = "用户角色 user/admin/ban")
    @ExcelProperty("用户角色")
    private String userRole;
    @Schema(description = "用户手机号")
    @ExcelProperty("用户手机号")
    @ColumnWidth(11)
    private String userPhone;
    @Schema(description = "用户真实姓名")
    @ExcelProperty("用户真实姓名")
    private String userRealName;
    @Schema(description = "性别：0-男性，1-女性")
    @ExcelProperty(value = "用户性别", converter = UserConvert.UserGenderConvert.class)
    private UserGenderEnum userGender;
    @Schema(description = "用户年龄")
    @ExcelProperty("用户年龄")
    private Integer userAge;
    @Schema(description = "用户邮箱")
    @ExcelProperty("用户邮箱")
    @ColumnWidth(25)
    private String userEmail;
    @Schema(description = "用户头像")
    @ExcelIgnore
    private String userAvatar;
    @Schema(description = "用户生日")
    @ExcelProperty("用户生日")
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(20)
    private LocalDate userBirthday;
    @Schema(description = "用户简介")
    @ExcelProperty("用户简介")
    private String userProfile;
    @Schema(description = "账户状态：0-禁用 1-正常")
    @ExcelProperty(value = "账户状态", converter = UserConvert.UserStatusConvert.class)
    private UserStatusEnum status;
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    @ColumnWidth(25)
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    @ExcelIgnore
    private LocalDateTime updateTime;
    @Schema(description = "创建者id")
    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT)
    private Long creater;
    @Schema(description = "更新者id")
    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    @Schema(description = "是否删除 0-未删除 1-已删除")
    @ExcelIgnore
    private Integer isDelete;
    @TableField(exist = false)
    @ExcelIgnore
    private static final long serialVersionUID = 1L;
}