package com.lt.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.mapper.RoleMapper;
import com.lt.boot.mapper.RoleMenuMapper;
import com.lt.boot.model.dto.role.RoleQuery;
import com.lt.boot.model.entity.Role;
import com.lt.boot.model.entity.RoleMenu;
import com.lt.boot.model.vo.role.RoleVO;
import com.lt.boot.service.RoleService;
import com.lt.boot.utils.CollUtils;
import com.lt.boot.utils.SqlUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public PageVO<RoleVO> listRoleByPage(RoleQuery query) {
        String roleName = query.getRoleName();
        String roleKey = query.getRoleKey();
        Integer status = query.getStatus();
        String sortBy = query.getSortBy();
        Boolean isAsc = query.getIsAsc();

        Page<Role> page = lambdaQuery()
                .like(StringUtils.isNotBlank(roleName), Role::getRoleName, roleName)
                .like(StringUtils.isNotBlank(roleKey), Role::getRoleKey, roleKey)
                .eq(status != null, Role::getStatus, status)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        if (SqlUtils.validSortField(sortBy)) {
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        }

        List<Role> roleList = page.getRecords();
        if (CollUtils.isEmpty(roleList)) {
            return PageVO.empty(page);
        }
        List<RoleVO> roleVOList = roleList.stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            return roleVO;
        }).collect(Collectors.toList());
        return PageVO.of(page, roleVOList);
    }

    @Override
    public List<RoleVO> listAllRoles() {
        List<Role> roleList = lambdaQuery()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getRoleSort)
                .list();
        if (CollUtils.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        return roleList.stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            return roleVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 校验角色是否存在
        Role role = getById(roleId);
        ThrowUtils.throwIf(role == null, ErrorCode.NOT_FOUND_ERROR, "角色不存在");

        // 删除旧的关联
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));

        // 插入新的关联
        if (CollUtils.isNotEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                int result = roleMenuMapper.insert(roleMenu);
                ThrowUtils.throwIf(result <= 0, ErrorCode.DB_SAVE_EXCEPTION, "分配菜单失败");
            }
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        // 校验角色是否存在
        Role role = getById(roleId);
        ThrowUtils.throwIf(role == null, ErrorCode.NOT_FOUND_ERROR, "角色不存在");

        List<RoleMenu> roleMenuList = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId));
        if (CollUtils.isEmpty(roleMenuList)) {
            return new ArrayList<>();
        }
        return roleMenuList.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
