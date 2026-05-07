package com.lt.boot.model.dto.user;

import com.lt.boot.constant.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/19 18:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户个人修改密码请求体")
public class UserUpdateMyPwdDTO extends UserUpdatePwdDTO implements Serializable {
    @Schema(description = "用户原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Pattern(regexp = RegexConstants.PASSWORD_PATTERN, message = "密码为6~32位的字母、数字、下划线")
    private String oldPassword;
}
