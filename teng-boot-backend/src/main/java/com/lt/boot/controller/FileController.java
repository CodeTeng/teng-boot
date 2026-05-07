package com.lt.boot.controller;

import com.lt.boot.annotation.LogRecord;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.constant.FileConstant;
import com.lt.boot.exception.BusinessException;
import com.lt.boot.manager.CosManager;
import com.lt.boot.model.dto.file.UploadFileRequest;
import com.lt.boot.model.entity.FileRecord;
import com.lt.boot.model.enums.FileUploadBizEnum;
import com.lt.boot.model.vo.user.UserVO;
import com.lt.boot.service.FileRecordService;
import com.lt.boot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/file")
@Slf4j
@Tag(name = "文件模块")
public class FileController {
    @Resource
    private UserService userService;
    @Resource
    private CosManager cosManager;
    @Resource
    private FileRecordService fileRecordService;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    @LogRecord("文件上传")
    public BaseResponse<String> uploadFile(@Parameter(description = "上传文件") @RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        UserVO user = userService.getCurrentUser();
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), user.getId(), filename);
        File file = null;
        try {
            file = File.createTempFile(filepath.replace("/", "_"), null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 保存文件记录
            FileRecord fileRecord = new FileRecord();
            fileRecord.setFileName(multipartFile.getOriginalFilename());
            fileRecord.setFileSize(multipartFile.getSize());
            fileRecord.setFileType(multipartFile.getContentType());
            fileRecord.setFileUrl(FileConstant.COS_HOST + filepath);
            fileRecord.setFileKey(filepath);
            fileRecord.setBizType(biz);
            fileRecord.setUserId(user.getId());
            fileRecordService.save(fileRecord);
            return ResultUtils.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        long fileSize = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        String fileSuffix = StringUtils.substringAfterLast(originalFilename, ".");
        final long ONE_M = 2 * 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
