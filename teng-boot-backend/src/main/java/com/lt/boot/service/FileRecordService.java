package com.lt.boot.service;

import com.lt.boot.common.page.PageVO;
import com.lt.boot.model.dto.file.FileQuery;
import com.lt.boot.model.entity.FileRecord;
import com.lt.boot.model.vo.file.FileRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author teng
 * @description 针对表【file_record(文件记录表)】的数据库操作Service
 * @createDate 2024-02-18 20:45:00
 */
public interface FileRecordService extends IService<FileRecord> {

    /**
     * 分页查询文件记录
     */
    PageVO<FileRecordVO> listFileByPage(FileQuery query);
}
