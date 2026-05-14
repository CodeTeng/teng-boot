package com.lt.boot.model.vo.user;

import com.lt.boot.model.enums.user.UserGenderEnum;
import com.lt.boot.model.enums.user.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "用户查询脱敏视图")
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户角色")
    private String userRole;
    @Schema(description = "用户手机号")
    private String userPhone;
    @Schema(description = "用户真实姓名")
    private String userRealName;
    @Schema(description = "性别：0-男性，1-女性")
    private UserGenderEnum userGender;
    @Schema(description = "用户年龄")
    private Integer userAge;
    @Schema(description = "用户邮箱")
    private String userEmail;
    @Schema(description = "用户头像")
    private String userAvatar;
    @Schema(description = "用户生日")
    private LocalDate userBirthday;
    @Schema(description = "用户简介")
    private String userProfile;
    @Schema(description = "账户状态：0-禁用 1-正常")
    private UserStatusEnum status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "创建者id")
    private Long creater;
    @Schema(description = "更新者id")
    private Long updater;
    @Schema(description = "用户token")
    private String token;
    @Schema(description = "RBAC角色名称")
    private String roleName;
    @Schema(description = "用户角色列表")
    private List<String> roles;
    @Schema(description = "用户权限标识列表")
    private List<String> permissions;
}