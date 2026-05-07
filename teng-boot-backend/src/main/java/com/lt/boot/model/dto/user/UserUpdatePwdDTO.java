package com.lt.boot.model.dto.user;

import com.lt.boot.constant.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/19 18:18
 */
@Data
@Schema(description = "修改密码请求体")
public class UserUpdatePwdDTO implements Serializable {
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Pattern(regexp = RegexConstants.PASSWORD_PATTERN, message = "密码为6~32位的字母、数字、下划线")
    private String userPassword;
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;
}
