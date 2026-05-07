package com.lt.boot.model.dto.user;

import com.lt.boot.common.page.PageQuery;
import com.lt.boot.model.enums.user.UserGenderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/19 14:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "前台用户分页查询请求体")
public class UserVOQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "用户名")
    private String username;
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
    @Schema(description = "用户简介")
    private String userProfile;
}
