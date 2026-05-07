package com.lt.boot.model.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "文件记录视图")
public class FileRecordVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件id")
    private Long id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件访问地址")
    private String fileUrl;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "上传用户id")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
