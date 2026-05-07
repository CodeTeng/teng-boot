package com.lt.boot.model.dto.file;

import com.lt.boot.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件分页查询请求体")
public class FileQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "业务类型")
    private String bizType;
}
