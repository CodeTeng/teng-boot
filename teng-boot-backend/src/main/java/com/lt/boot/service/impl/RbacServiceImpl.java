package com.lt.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.mapper.MenuMapper;
import com.lt.boot.mapper.RoleMapper;
import com.lt.boot.mapper.RoleMenuMapper;
import com.lt.boot.mapper.UserMapper;
import com.lt.boot.mapper.UserRoleMapper;
import com.lt.boot.model.entity.Menu;
import com.lt.boot.model.entity.Role;
import com.lt.boot.model.entity.RoleMenu;
import com.lt.boot.model.entity.User;
import com.lt.boot.model.entity.UserRole;
import com.lt.boot.service.RbacService;
import com.lt.boot.utils.CollUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RBAC 权限服务实现
 *
 * @author teng
 * @description 提供角色和权限相关的查询与分配功能
 */
@Service
public class RbacServiceImpl implements RbacService {

    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<String> getUserRoleKeys(Long userId) {
        List<Long> roleIds = getUserRoleIds(userId);
        if (CollUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        if (CollUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(r -> r.getStatus() == 1)
                .map(Role::getRoleKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId));
        if (CollUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        return userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId));
        if (CollUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds));
        if (CollUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }
        List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menus = menuMapper.selectBatchIds(menuIds);
        if (CollUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        return menus.stream()
                .filter(m -> m.getStatus() == 1)
                .filter(m -> StringUtils.isNotBlank(m.getPerms()))
                .map(Menu::getPerms)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignDefaultRole(Long userId) {
        Role defaultRole = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleKey, UserConstant.DEFAULT_ROLE));
        if (defaultRole != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(defaultRole.getId());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional
    public void assignRole(Long userId, String roleKey) {
        Role role = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleKey, roleKey));
        if (role != null) {
            userRoleMapper.delete(
                    new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, userId));
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional
    public void assignRoleByUsername(String username, String roleKey) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username));
        if (user != null) {
            assignRole(user.getId(), roleKey);
        }
    }
}
