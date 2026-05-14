package com.lt.boot.service;

import java.util.List;

/**
 * RBAC 权限服务接口
 *
 * @author teng
 * @description 提供角色和权限相关的查询与分配功能
 */
public interface RbacService {

    /**
     * 获取用户的角色 key 列表
     *
     * @param userId 用户ID
     * @return 角色 key 列表
     */
    List<String> getUserRoleKeys(Long userId);

    /**
     * 获取用户的角色 ID 列表
     *
     * @param userId 用户ID
     * @return 角色 ID 列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户的权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 为用户分配默认角色（user）
     *
     * @param userId 用户ID
     */
    void assignDefaultRole(Long userId);

    /**
     * 为用户指定角色
     *
     * @param userId  用户ID
     * @param roleKey 角色 key
     */
    void assignRole(Long userId, String roleKey);

    /**
     * 根据用户名分配角色
     *
     * @param username 用户名
     * @param roleKey  角色 key
     */
    void assignRoleByUsername(String username, String roleKey);
}
