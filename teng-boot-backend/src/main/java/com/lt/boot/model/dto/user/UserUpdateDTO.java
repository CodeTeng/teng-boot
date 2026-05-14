package com.lt.boot.model.dto.user;

import com.lt.boot.common.validate.annotation.EnumValid;
import com.lt.boot.constant.RegexConstants;
import com.lt.boot.model.enums.user.UserGenderEnum;
import com.lt.boot.model.enums.user.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "更新用户请求体")
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户id不能为空")
    private Long id;
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = RegexConstants.USERNAME_PATTERN, message = "用户名为4~32位的字母、数字、下划线")
    private String username;
    @Schema(description = "用户角色集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户角色不能为空")
    private String userRole;
    @Schema(description = "用户手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegexConstants.PHONE_PATTERN, message = "请填写正确的手机号")
    private String userPhone;
    @Schema(description = "用户真实姓名")
    private String userRealName;
    @Schema(description = "性别：0-男性，1-女性", requiredMode = Schema.RequiredMode.REQUIRED)
    @EnumValid(enumeration = {0, 1}, message = "用户性别不符合")
    private UserGenderEnum userGender;
    @Schema(description = "用户年龄")
    @Min(value = 0L, message = "请填写正确的年龄")
    @Max(value = 120L, message = "请填写正确的年龄")
    private Integer userAge;
    @Schema(description = "用户邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请填写正确的邮箱格式")
    private String userEmail;
    @Schema(description = "用户头像")
    private String userAvatar;
    @Schema(description = "用户生日")
    private LocalDate userBirthday;
    @Schema(description = "用户简介")
    private String userProfile;
    @Schema(description = "账户状态：0-禁用 1-正常", requiredMode = Schema.RequiredMode.REQUIRED)
    @EnumValid(enumeration = {0, 1}, message = "用户状态不符合")
    private UserStatusEnum status;
}