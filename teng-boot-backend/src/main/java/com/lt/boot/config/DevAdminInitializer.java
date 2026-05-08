package com.lt.boot.config;

import com.lt.boot.service.RbacService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 开发环境：自动为 e2eadmin 用户分配 admin 角色
 */
@Slf4j
@Component
public class DevAdminInitializer {

    @Lazy @Resource private RbacService rbacService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            rbacService.assignRoleByUsername("e2eadmin", "admin");
            log.info("DevAdminInitializer: e2eadmin → admin role assigned");
        } catch (Exception e) {
            log.warn("DevAdminInitializer: {}", e.getMessage());
        }
    }
}
