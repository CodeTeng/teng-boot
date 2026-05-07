package com.lt.boot.service;

import com.lt.boot.common.page.PageVO;
import com.lt.boot.model.dto.role.RoleQuery;
import com.lt.boot.model.entity.Role;
import com.lt.boot.model.vo.role.RoleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author teng
 * @description 针对表【role(角色表)】的数据库操作Service
 * @createDate 2024-02-18 20:45:00
 */
public interface RoleService extends IService<Role> {

    /**
     * 角色列表（含分页）
     */
    PageVO<RoleVO> listRoleByPage(RoleQuery query);

    /**
     * 所有角色（不分页）
     */
    List<RoleVO> listAllRoles();

    /**
     * 为角色分配菜单
     */
    void assignRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色拥有的菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);
}
