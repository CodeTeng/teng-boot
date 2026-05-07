package com.lt.boot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.boot.mapper.*;
import com.lt.boot.model.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Lazy @Resource private RoleMapper roleMapper;
    @Lazy @Resource private MenuMapper menuMapper;
    @Lazy @Resource private RoleMenuMapper roleMenuMapper;
    @Lazy @Resource private UserRoleMapper userRoleMapper;

    @Override
    public void run(String... args) {
        Long roleCount = roleMapper.selectCount(new LambdaQueryWrapper<>());
        if (roleCount > 0) {
            log.info("RBAC 数据已存在，跳过初始化");
            return;
        }
        log.info("开始初始化 RBAC 基础数据...");

        Role adminRole = newRole(1L, "超级管理员", "admin", 1, "拥有所有权限");
        Role userRole = newRole(2L, "普通用户", "user", 2, "普通用户权限");
        roleMapper.insert(adminRole);
        roleMapper.insert(userRole);

        List<Menu> menus = buildMenus();
        menus.forEach(menuMapper::insert);

        menus.forEach(m -> roleMenuMapper.insert(newRoleMenu(1L, m.getId())));
        roleMenuMapper.insert(newRoleMenu(2L, 16L));
        roleMenuMapper.insert(newRoleMenu(2L, 17L));

        log.info("RBAC 初始化完成：{} 角色，{} 菜单", roleMapper.selectCount(null), menuMapper.selectCount(null));
    }

    private Role newRole(Long id, String name, String key, int sort, String remark) {
        Role r = new Role();
        r.setId(id); r.setRoleName(name); r.setRoleKey(key); r.setRoleSort(sort); r.setStatus(1); r.setRemark(remark);
        return r;
    }
    private RoleMenu newRoleMenu(Long roleId, Long menuId) {
        RoleMenu rm = new RoleMenu();
        rm.setRoleId(roleId); rm.setMenuId(menuId);
        return rm;
    }

    private List<Menu> buildMenus() {
        return List.of(
            m(1L, "系统管理", 0L, 1, "/system", null, "M", "Setting"),
            m(2L, "用户管理", 1L, 1, "user", "system/user/index.vue", "C", "User", "system:user:list"),
            m(3L, "用户新增", 2L, 1, null, null, "F", null, "system:user:add"),
            m(4L, "用户编辑", 2L, 1, null, null, "F", null, "system:user:edit"),
            m(5L, "用户删除", 2L, 1, null, null, "F", null, "system:user:delete"),
            m(6L, "角色管理", 1L, 2, "role", "system/role/index.vue", "C", "UserFilled", "system:role:list"),
            m(7L, "角色新增", 6L, 1, null, null, "F", null, "system:role:add"),
            m(8L, "角色编辑", 6L, 1, null, null, "F", null, "system:role:edit"),
            m(9L, "角色删除", 6L, 1, null, null, "F", null, "system:role:delete"),
            m(10L, "菜单管理", 1L, 3, "menu", "system/menu/index.vue", "C", "Menu", "system:menu:list"),
            m(11L, "菜单新增", 10L, 1, null, null, "F", null, "system:menu:add"),
            m(12L, "菜单编辑", 10L, 1, null, null, "F", null, "system:menu:edit"),
            m(13L, "菜单删除", 10L, 1, null, null, "F", null, "system:menu:delete"),
            m(14L, "系统监控", 0L, 2, "/monitor", null, "M", "Monitor"),
            m(15L, "操作日志", 14L, 1, "log", "monitor/log/index.vue", "C", "Document", "monitor:log:list"),
            m(16L, "文件管理", 0L, 3, "/file", null, "M", "Folder"),
            m(17L, "文件列表", 16L, 1, "list", "file/list/index.vue", "C", "Files", "file:list"),
            m(18L, "文件删除", 17L, 1, null, null, "F", null, "file:delete")
        );
    }

    private Menu m(Long id, String name, Long pid, int order, String path, String comp, String type, String icon) {
        return m(id, name, pid, order, path, comp, type, icon, null);
    }
    private Menu m(Long id, String name, Long pid, int order, String path, String comp, String type, String icon, String perms) {
        Menu menu = new Menu();
        menu.setId(id); menu.setMenuName(name); menu.setParentId(pid); menu.setOrderNum(order);
        menu.setPath(path); menu.setComponent(comp); menu.setMenuType(type); menu.setVisible(1);
        menu.setStatus(1); menu.setIcon(icon); menu.setPerms(perms);
        return menu;
    }
}
