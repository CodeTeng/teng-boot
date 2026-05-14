package com.lt.boot.controller.admin;

import com.lt.boot.annotation.AuthCheck;
import com.lt.boot.annotation.LogRecord;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.model.dto.role.RoleAddDTO;
import com.lt.boot.model.dto.role.RoleQuery;
import com.lt.boot.model.dto.role.RoleUpdateDTO;
import com.lt.boot.model.entity.Role;
import com.lt.boot.model.vo.role.RoleVO;
import com.lt.boot.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/admin/roles")
@Slf4j
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/list/page")
    @Operation(summary = "分页查询角色列表")
    public BaseResponse<PageVO<RoleVO>> listRoleByPage(@RequestBody @Validated RoleQuery query) {
        ThrowUtils.throwIf(query == null, ErrorCode.PARAMS_ERROR);
        PageVO<RoleVO> page = roleService.listRoleByPage(query);
        return ResultUtils.success(page);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有角色（不分页）")
    public BaseResponse<List<RoleVO>> listAllRoles() {
        List<RoleVO> roleVOList = roleService.listAllRoles();
        return ResultUtils.success(roleVOList);
    }

    @PostMapping
    @Operation(summary = "添加角色")
    @LogRecord("添加角色")
    public BaseResponse<Boolean> addRole(@RequestBody @Validated RoleAddDTO roleAddDTO) {
        ThrowUtils.throwIf(roleAddDTO == null, ErrorCode.PARAMS_ERROR);
        Role role = new Role();
        BeanUtils.copyProperties(roleAddDTO, role);
        boolean result = roleService.save(role);
        ThrowUtils.throwIf(!result, ErrorCode.DB_SAVE_EXCEPTION);
        return ResultUtils.success(true);
    }

    @PutMapping
    @Operation(summary = "修改角色")
    @LogRecord("修改角色")
    public BaseResponse<Boolean> updateRole(@RequestBody @Validated RoleUpdateDTO roleUpdateDTO) {
        ThrowUtils.throwIf(roleUpdateDTO == null, ErrorCode.PARAMS_ERROR);
        Role role = new Role();
        BeanUtils.copyProperties(roleUpdateDTO, role);
        boolean result = roleService.updateById(role);
        ThrowUtils.throwIf(!result, ErrorCode.DB_UPDATE_EXCEPTION);
        return ResultUtils.success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @LogRecord("删除角色")
    public BaseResponse<Boolean> deleteRole(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        roleService.deleteRole(id);
        return ResultUtils.success(true);
    }

    @PostMapping("/{roleId}/menus")
    @Operation(summary = "为角色分配菜单")
    @LogRecord("为角色分配菜单")
    public BaseResponse<Boolean> assignRoleMenus(@PathVariable("roleId") Long roleId,
                                                  @RequestBody List<Long> menuIds) {
        ThrowUtils.throwIf(roleId == null || roleId <= 0, ErrorCode.PARAMS_ERROR);
        roleService.assignRoleMenus(roleId, menuIds);
        return ResultUtils.success(true);
    }

    @GetMapping("/{roleId}/menus")
    @Operation(summary = "获取角色拥有的菜单ID列表")
    public BaseResponse<List<Long>> getRoleMenuIds(@PathVariable("roleId") Long roleId) {
        ThrowUtils.throwIf(roleId == null || roleId <= 0, ErrorCode.PARAMS_ERROR);
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return ResultUtils.success(menuIds);
    }
}
