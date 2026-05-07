package com.lt.boot.config.mp;

import com.lt.boot.model.entity.Role;
import com.lt.boot.service.RoleService;
import com.lt.boot.utils.UserThreadLocalUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyMetaObjectHandler 自动填充集成测试
 * <p>
 * 通过 RoleService 进行 INSERT / UPDATE 操作，验证 MyMetaObjectHandler
 * 是否正确填充 creater 和 updater 字段。
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("MyMetaObjectHandler 自动填充测试")
class MyMetaObjectHandlerTest {

    @Resource
    private RoleService roleService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @AfterEach
    void tearDown() {
        UserThreadLocalUtils.clear();
    }

    // ==================== insertFill ====================

    @Test
    @DisplayName("insertFill - 新增角色时 creater 被正确填充")
    void testInsertFill_ShouldSetCreater() {
        // given
        UserThreadLocalUtils.setUserId(100L);
        Role role = new Role();
        role.setRoleName("测试角色 TDD");
        role.setRoleKey("test_tdd_role");
        role.setRoleSort(1);
        role.setStatus(1);

        // when
        boolean saved = roleService.save(role);

        // then
        assertTrue(saved, "角色保存应成功");
        assertNotNull(role.getId(), "角色 ID 应被自动生成");
        assertEquals(100L, role.getCreater(), "creater 应为当前登录用户 ID");
    }

    @Test
    @DisplayName("insertFill - 未登录时新增角色 creater 设为 0")
    void testInsertFill_WhenNoLogin_ShouldSetZero() {
        // given (未设置 UserThreadLocalUtils, userId 为 null)
        Role role = new Role();
        role.setRoleName("测试角色 TDD 未登录");
        role.setRoleKey("test_tdd_role_nologin");
        role.setRoleSort(2);
        role.setStatus(1);

        // when
        boolean saved = roleService.save(role);

        // then
        assertTrue(saved, "角色保存应成功");
        assertEquals(0L, role.getCreater(), "未登录时 creater 应为 0");
    }

    // ==================== updateFill ====================

    @Test
    @DisplayName("updateFill - 更新角色时 updater 被正确填充")
    void testUpdateFill_ShouldSetUpdater() {
        // given - 先创建一个角色
        UserThreadLocalUtils.setUserId(300L);
        Role role = new Role();
        role.setRoleName("测试角色 TDD 更新前");
        role.setRoleKey("test_tdd_role_update");
        role.setRoleSort(3);
        role.setStatus(1);
        roleService.save(role);
        Long roleId = role.getId();
        assertNotNull(roleId, "角色 ID 不应为 null");

        // when - 更新角色
        UserThreadLocalUtils.setUserId(400L);
        Role updateRole = new Role();
        updateRole.setId(roleId);
        updateRole.setRoleName("测试角色 TDD 更新后");
        boolean updated = roleService.updateById(updateRole);

        // then
        assertTrue(updated, "角色更新应成功");
        Role refreshedRole = roleService.getById(roleId);
        assertNotNull(refreshedRole, "更新后的角色不应为 null");
        assertEquals(400L, refreshedRole.getUpdater(), "updater 应为当前登录用户 ID");
    }

    @Test
    @DisplayName("updateFill - 未登录时更新角色 updater 设为 0")
    void testUpdateFill_WhenNoLogin_ShouldSetZero() {
        // given - 先创建一个角色（已登录）
        UserThreadLocalUtils.setUserId(500L);
        Role role = new Role();
        role.setRoleName("测试角色 TDD 未登录更新");
        role.setRoleKey("test_tdd_role_upd_nologin");
        role.setRoleSort(4);
        role.setStatus(1);
        roleService.save(role);
        Long roleId = role.getId();
        assertNotNull(roleId, "角色 ID 不应为 null");
        UserThreadLocalUtils.clear();

        // when - 更新角色（未登录）
        Role updateRole = new Role();
        updateRole.setId(roleId);
        updateRole.setRoleName("测试角色 TDD 未登录更新后");
        boolean updated = roleService.updateById(updateRole);

        // then
        assertTrue(updated, "角色更新应成功");
        Role refreshedRole = roleService.getById(roleId);
        assertNotNull(refreshedRole, "更新后的角色不应为 null");
        assertEquals(0L, refreshedRole.getUpdater(), "未登录时 updater 应为 0");
    }
}
