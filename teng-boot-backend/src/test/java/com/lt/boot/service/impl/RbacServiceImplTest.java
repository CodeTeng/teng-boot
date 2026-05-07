package com.lt.boot.service.impl;

import com.lt.boot.mapper.RoleMapper;
import com.lt.boot.mapper.UserRoleMapper;
import com.lt.boot.model.entity.Role;
import com.lt.boot.model.entity.UserRole;
import com.lt.boot.service.RbacService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RbacService 单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class RbacServiceImplTest {

    @Resource
    private RbacService rbacService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @Test
    void testGetUserRoleKeys_NoRoles() {
        // 用户 999 没有分配任何角色
        List<String> roleKeys = rbacService.getUserRoleKeys(999L);
        assertTrue(roleKeys.isEmpty());
    }

    @Test
    void testAssignDefaultRole() {
        // 分配默认角色
        rbacService.assignDefaultRole(100L);

        // 验证 user_role 关联已插入
        List<UserRole> userRoles = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, 100L));
        assertFalse(userRoles.isEmpty());

        // 验证关联的角色是 user（默认角色）
        Role role = roleMapper.selectById(userRoles.get(0).getRoleId());
        assertNotNull(role);
        assertEquals("user", role.getRoleKey());
    }

    @Test
    void testAssignRole() {
        // 先分配默认角色
        rbacService.assignDefaultRole(101L);

        // 再分配 admin 角色
        rbacService.assignRole(101L, "admin");

        // 验证旧角色已被清除，只有新角色
        List<UserRole> userRoles = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, 101L));
        assertEquals(1, userRoles.size());
        Role role = roleMapper.selectById(userRoles.get(0).getRoleId());
        assertEquals("admin", role.getRoleKey());
    }

    @Test
    void testGetUserRoleKeys_WithRoles() {
        // 为用户 102 分配 user 角色
        rbacService.assignDefaultRole(102L);

        List<String> roleKeys = rbacService.getUserRoleKeys(102L);
        assertFalse(roleKeys.isEmpty());
        assertTrue(roleKeys.contains("user"));
    }

    @Test
    void testGetUserPermissions_NoRoles() {
        // 用户 999 没有角色，不应有权限
        List<String> permissions = rbacService.getUserPermissions(999L);
        assertTrue(permissions.isEmpty());
    }

    @Test
    void testGetUserPermissions_WithUserRole() {
        // 为用户 103 分配 user 角色
        rbacService.assignDefaultRole(103L);

        List<String> permissions = rbacService.getUserPermissions(103L);
        // user 角色有 system:user:list 和 system:user:query 权限
        assertTrue(permissions.contains("system:user:list"));
        assertTrue(permissions.contains("system:user:query"));
    }

    @Test
    void testGetUserPermissions_WithAdminRole() {
        // 为用户 104 分配 admin 角色
        rbacService.assignRole(104L, "admin");

        List<String> permissions = rbacService.getUserPermissions(104L);
        // admin 角色有所有权限
        assertTrue(permissions.contains("system:user:list"));
        assertTrue(permissions.contains("system:user:query"));
        assertTrue(permissions.contains("system:user:add"));
        assertTrue(permissions.contains("system:user:edit"));
        assertTrue(permissions.contains("system:user:remove"));
    }
}
