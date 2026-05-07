package com.lt.boot.controller;

import com.lt.boot.annotation.AuthCheck;
import com.lt.boot.annotation.LogRecord;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.DeleteRequest;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.exception.BusinessException;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.model.dto.user.*;
import com.lt.boot.model.entity.User;
import com.lt.boot.model.enums.user.UserStatusEnum;
import com.lt.boot.model.vo.user.UserVO;
import com.lt.boot.service.UserService;
import com.lt.boot.utils.CollUtils;
import com.lt.boot.utils.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/2/17 20:51
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<UserVO> userLogin(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        ThrowUtils.throwIf(userLoginDTO == null, ErrorCode.PARAMS_ERROR);
        UserVO userVO = userService.userLogin(userLoginDTO);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<Boolean> userRegister(@RequestBody @Validated UserRegisterDTO userRegisterDTO) {
        ThrowUtils.throwIf(userRegisterDTO == null, ErrorCode.PARAMS_ERROR);
        // 密码和校验密码相同
        if (!userRegisterDTO.getUserPassword().equals(userRegisterDTO.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        userService.userRegister(userRegisterDTO);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/login")
    @Operation(summary = "获取当前登录用户")
    public BaseResponse<UserVO> getLoginUser() {
        UserVO currentUser = userService.getCurrentUser();
        return ResultUtils.success(currentUser);
    }

    @PutMapping("/update/myPwd")
    @Operation(summary = "当前用户个人修改密码")
    @LogRecord("当前用户个人修改密码")
    public BaseResponse<Boolean> updateUserMyPwd(@RequestBody @Validated UserUpdateMyPwdDTO userUpdateMyPwdDTO) {
        ThrowUtils.throwIf(userUpdateMyPwdDTO == null, ErrorCode.PARAMS_ERROR);
        if (!userUpdateMyPwdDTO.getUserPassword().equals(userUpdateMyPwdDTO.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        userService.updateUserMyPwd(userUpdateMyPwdDTO);
        return ResultUtils.success(true);
    }

    @PostMapping("/list/page/vo")
    @Operation(summary = "前台分页查询用户 默认按照创建时间降序，还可以根据用户年龄，用户生日，更新时间排序")
    public BaseResponse<PageVO<UserVO>> listUserVOByPage(@RequestBody @Validated UserVOQuery userVOQuery) {
        ThrowUtils.throwIf(userVOQuery == null, ErrorCode.PARAMS_ERROR);
        // 简单限制爬虫
        ThrowUtils.throwIf(userVOQuery.getPageSize() > 50, ErrorCode.OPERATION_ERROR);
        PageVO<UserVO> page = userService.listUserVOByPage(userVOQuery);
        return ResultUtils.success(page);
    }

    @GetMapping("/get/vo/{id}")
    @Operation(summary = "前台根据id获取用户")
    public BaseResponse<UserVO> getUserVOById(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (user.getStatus() == UserStatusEnum.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKOUT);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @PutMapping("/update/my")
    @Operation(summary = "更新个人信息")
    @LogRecord("更新个人信息")
    public BaseResponse<Boolean> updateMyUser(@RequestBody @Validated UserUpdateMyDTO userUpdateMyDTO) {
        ThrowUtils.throwIf(userUpdateMyDTO == null, ErrorCode.PARAMS_ERROR);
        userService.updateMyUser(userUpdateMyDTO);
        return ResultUtils.success(true);
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户(后台管理)")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @LogRecord("添加用户(后台管理)")
    public BaseResponse<Boolean> addUser(@RequestBody @Validated UserAddDTO userAddDTO) {
        ThrowUtils.throwIf(userAddDTO == null, ErrorCode.PARAMS_ERROR);
        userService.addUser(userAddDTO);
        return ResultUtils.success(true, "你已成功添加用户，默认密码为：" + UserConstant.DEFAULT_USER_PASSWORD);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户(单个和批量)(后台管理)")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @LogRecord("删除用户(单个和批量)(后台管理)")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || CollUtils.isEmpty(deleteRequest.getIds()), ErrorCode.PARAMS_ERROR);
        List<Long> idList = deleteRequest.getIds();
        // 限制批量操作的数量
        if (idList.size() > 50) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量操作数量不能超过50");
        }
        boolean result = userService.removeBatchByIds(idList);
        ThrowUtils.throwIf(!result, ErrorCode.DB_DELETE_EXCEPTION);
        return ResultUtils.success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户(后台管理)")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @LogRecord("修改用户(后台管理)")
    public BaseResponse<Boolean> updateUser(@RequestBody @Validated UserUpdateDTO userUpdateDTO) {
        ThrowUtils.throwIf(userUpdateDTO == null, ErrorCode.PARAMS_ERROR);
        userService.updateUser(userUpdateDTO);
        return ResultUtils.success(true);
    }

    @PutMapping("/update/pwd/{id}")
    @Operation(summary = "修改密码(后台管理)")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @LogRecord("修改密码(后台管理)")
    public BaseResponse<Boolean> updateUserPwd(@PathVariable("id") Long id, @RequestBody @Validated UserUpdatePwdDTO userUpdatePwdDTO) {
        ThrowUtils.throwIf(userUpdatePwdDTO == null || id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        if (!userUpdatePwdDTO.getUserPassword().equals(userUpdatePwdDTO.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        userService.updateUserPwd(id, userUpdatePwdDTO);
        return ResultUtils.success(true);
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页查询用户(后台管理)默认按照创建时间降序，还可以根据用户年龄，用户生日，更新时间排序")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageVO<User>> listUserByPage(@RequestBody @Validated UserQuery userQuery) {
        ThrowUtils.throwIf(userQuery == null, ErrorCode.PARAMS_ERROR);
        PageVO<User> page = userService.listUserByPage(userQuery);
        return ResultUtils.success(page);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "根据id获取用户(后台管理)")
    @Parameter(name = "id", description = "用户id", required = true)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    @PostMapping("/export/page")
    @Operation(summary = "用户导出 根据条件进行导出")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @LogRecord("用户数据导出(后台管理)")
    public BaseResponse<Boolean> exportUser(@RequestBody @Validated UserQuery userQuery, HttpServletResponse response) throws Exception {
        ThrowUtils.throwIf(userQuery == null, ErrorCode.PARAMS_ERROR);
        PageVO<User> page = userService.listUserByPage(userQuery);
        List<User> list = page.getList();
        ExcelUtils.doExport(list, User.class, "用户数据", response);
        return ResultUtils.success(true);
    }
}
