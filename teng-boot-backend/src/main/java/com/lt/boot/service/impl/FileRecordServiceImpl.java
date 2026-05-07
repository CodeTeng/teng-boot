package com.lt.boot.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.mapper.FileRecordMapper;
import com.lt.boot.model.dto.file.FileQuery;
import com.lt.boot.model.entity.FileRecord;
import com.lt.boot.model.vo.file.FileRecordVO;
import com.lt.boot.service.FileRecordService;
import com.lt.boot.utils.CollUtils;
import com.lt.boot.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileRecordServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord>
        implements FileRecordService {

    @Override
    public PageVO<FileRecordVO> listFileByPage(FileQuery query) {
        String fileName = query.getFileName();
        String fileType = query.getFileType();
        String bizType = query.getBizType();
        String sortBy = query.getSortBy();
        Boolean isAsc = query.getIsAsc();

        Page<FileRecord> page = lambdaQuery()
                .like(StringUtils.isNotBlank(fileName), FileRecord::getFileName, fileName)
                .like(StringUtils.isNotBlank(fileType), FileRecord::getFileType, fileType)
                .eq(StringUtils.isNotBlank(bizType), FileRecord::getBizType, bizType)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        if (SqlUtils.validSortField(sortBy)) {
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        }

        List<FileRecord> recordList = page.getRecords();
        if (CollUtils.isEmpty(recordList)) {
            return PageVO.empty(page);
        }
        List<FileRecordVO> voList = recordList.stream().map(record -> {
            FileRecordVO vo = new FileRecordVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());
        return PageVO.of(page, voList);
    }
}
