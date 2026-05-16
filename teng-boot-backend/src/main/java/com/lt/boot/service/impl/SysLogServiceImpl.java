package com.lt.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.mapper.SysLogMapper;
import com.lt.boot.model.dto.log.SysLogQuery;
import com.lt.boot.model.entity.SysLog;
import com.lt.boot.service.SysLogService;
import com.lt.boot.utils.CollUtils;
import com.lt.boot.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author teng
 * @description 针对表【sys_log(系统日志表)】的数据库操作Service实现
 * @createDate 2024-02-18 20:45:00
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog>
        implements SysLogService {
    @Override
    public PageVO<SysLog> listSysLogByPage(SysLogQuery sysLogQuery) {
        Long id = sysLogQuery.getId();
        Long userId = sysLogQuery.getUserId();
        String username = sysLogQuery.getUsername();
        String value = sysLogQuery.getValue();
        String operation = sysLogQuery.getOperation();
        String url = sysLogQuery.getUrl();
        String methodName = sysLogQuery.getMethodName();
        String ip = sysLogQuery.getIp();
        String os = sysLogQuery.getOs();
        Integer logType = sysLogQuery.getLogType();
        Boolean isAsc = sysLogQuery.getIsAsc();
        String sortBy = sysLogQuery.getSortBy();

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, SysLog::getId, id)
                .eq(userId != null, SysLog::getUserId, userId)
                .eq(StringUtils.isNotBlank(operation), SysLog::getOperation, operation)
                .like(StringUtils.isNotBlank(username), SysLog::getUsername, username)
                .like(StringUtils.isNotBlank(value), SysLog::getValue, value)
                .like(StringUtils.isNotBlank(url), SysLog::getUrl, url)
                .like(StringUtils.isNotBlank(methodName), SysLog::getMethodName, methodName)
                .like(StringUtils.isNotBlank(ip), SysLog::getIp, ip)
                .eq(StringUtils.isNotBlank(os), SysLog::getOs, os);

        // 处理日志类型筛选
        // 注意：如果数据库 sys_log 表没有 log_type 字段，查询会报错
        // 请先执行 sql/update_sys_log_log_type.sql 脚本添加字段
        if (logType != null) {
            // 指定了日志类型（1:操作日志）
            wrapper.eq(SysLog::getLogType, logType);
        }
        // logType 为 null 时，不进行类型筛选，返回所有日志
        // 避免在没有 log_type 字段时查询报错

        Page<SysLog> page = page(sysLogQuery.toMpPageDefaultSortByCreateTimeDesc(), wrapper);
        if (SqlUtils.validSortField(sortBy)) {
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        }
        List<SysLog> sysLogList = page.getRecords();
        if (CollUtils.isEmpty(sysLogList)) {
            return PageVO.empty(page);
        }
        return PageVO.of(page, sysLogList);
    }
}