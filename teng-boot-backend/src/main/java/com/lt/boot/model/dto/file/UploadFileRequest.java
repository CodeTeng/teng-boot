package com.lt.boot.model.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传请求
 */
@Data
@Schema(description = "文件上传请求体")
public class UploadFileRequest implements Serializable {
    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_avatar")
    @NotBlank(message = "业务类型不能为空")
    private String biz;
    private static final long serialVersionUID = 1L;
}