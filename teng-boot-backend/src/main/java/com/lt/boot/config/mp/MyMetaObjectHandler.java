package com.lt.boot.config.mp;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lt.boot.utils.UserThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/17 22:49
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = UserThreadLocalUtils.getUserId();
        this.strictInsertFill(metaObject, "creater", () -> userId == null ? 0L : userId, Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = UserThreadLocalUtils.getUserId();
        this.strictUpdateFill(metaObject, "updater", () -> userId == null ? 0L : userId, Long.class);
    }
}
