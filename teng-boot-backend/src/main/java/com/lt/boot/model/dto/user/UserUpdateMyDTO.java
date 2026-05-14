package com.lt.boot.model.dto.user;

import com.lt.boot.common.validate.annotation.EnumValid;
import com.lt.boot.constant.RegexConstants;
import com.lt.boot.model.enums.user.UserGenderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Schema(description = "用户更新个人信息请求体")
public class UserUpdateMyDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = RegexConstants.USERNAME_PATTERN, message = "用户名为4~32位的字母、数字、下划线")
    private String username;
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
}