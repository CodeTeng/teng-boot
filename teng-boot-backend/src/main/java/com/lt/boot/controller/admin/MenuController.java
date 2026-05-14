package com.lt.boot.controller.admin;

import com.lt.boot.annotation.AuthCheck;
import com.lt.boot.annotation.LogRecord;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.model.dto.menu.MenuAddDTO;
import com.lt.boot.model.dto.menu.MenuUpdateDTO;
import com.lt.boot.model.entity.Menu;
import com.lt.boot.model.vo.menu.MenuVO;
import com.lt.boot.service.MenuService;
import com.lt.boot.service.RbacService;
import com.lt.boot.utils.UserThreadLocalUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/admin/menus")
@Slf4j
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private RbacService rbacService;

    @GetMapping("/tree")
    @Operation(summary = "获取完整菜单树（管理端）")
    public BaseResponse<List<MenuVO>> listAllMenuTree() {
        List<MenuVO> menuTree = menuService.listAllMenuTree();
        return ResultUtils.success(menuTree);
    }

    @GetMapping("/user-tree")
    @Operation(summary = "获取当前用户的菜单树")
    public BaseResponse<List<MenuVO>> listMenuTree() {
        Long userId = UserThreadLocalUtils.getUserId();
        List<Long> roleIds = rbacService.getUserRoleIds(userId);
        List<MenuVO> menuTree = menuService.listMenuTreeByRoleIds(roleIds);
        return ResultUtils.success(menuTree);
    }

    @PostMapping
    @Operation(summary = "添加菜单")
    @LogRecord("添加菜单")
    public BaseResponse<Boolean> addMenu(@RequestBody @Validated MenuAddDTO menuAddDTO) {
        ThrowUtils.throwIf(menuAddDTO == null, ErrorCode.PARAMS_ERROR);
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuAddDTO, menu);
        boolean result = menuService.save(menu);
        ThrowUtils.throwIf(!result, ErrorCode.DB_SAVE_EXCEPTION);
        return ResultUtils.success(true);
    }

    @PutMapping
    @Operation(summary = "修改菜单")
    @LogRecord("修改菜单")
    public BaseResponse<Boolean> updateMenu(@RequestBody @Validated MenuUpdateDTO menuUpdateDTO) {
        ThrowUtils.throwIf(menuUpdateDTO == null, ErrorCode.PARAMS_ERROR);
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuUpdateDTO, menu);
        boolean result = menuService.updateById(menu);
        ThrowUtils.throwIf(!result, ErrorCode.DB_UPDATE_EXCEPTION);
        return ResultUtils.success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @LogRecord("删除菜单")
    public BaseResponse<Boolean> deleteMenu(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = menuService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.DB_DELETE_EXCEPTION);
        return ResultUtils.success(true);
    }
}
