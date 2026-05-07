package com.lt.boot.model.dto.user;

import com.lt.boot.constant.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
@Schema(description = "用户注册请求体")
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = RegexConstants.USERNAME_PATTERN, message = "用户名为4~32位的字母、数字、下划线")
    private String username;
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Pattern(regexp = RegexConstants.PASSWORD_PATTERN, message = "密码为6~32位的字母、数字、下划线")
    private String userPassword;
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;
}
