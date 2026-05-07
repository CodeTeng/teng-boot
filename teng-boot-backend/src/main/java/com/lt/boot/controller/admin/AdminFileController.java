package com.lt.boot.controller.admin;

import com.lt.boot.annotation.AuthCheck;
import com.lt.boot.annotation.LogRecord;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.model.dto.file.FileQuery;
import com.lt.boot.model.vo.file.FileRecordVO;
import com.lt.boot.service.FileRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/admin/files")
@Slf4j
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public class AdminFileController {

    @Resource
    private FileRecordService fileRecordService;

    @PostMapping("/list/page")
    @Operation(summary = "分页查询文件记录")
    public BaseResponse<PageVO<FileRecordVO>> listFileByPage(@RequestBody @Validated FileQuery query) {
        ThrowUtils.throwIf(query == null, ErrorCode.PARAMS_ERROR);
        PageVO<FileRecordVO> page = fileRecordService.listFileByPage(query);
        return ResultUtils.success(page);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件记录")
    @LogRecord("删除文件记录")
    public BaseResponse<Boolean> deleteFile(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = fileRecordService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.DB_DELETE_EXCEPTION);
        return ResultUtils.success(true);
    }
}
