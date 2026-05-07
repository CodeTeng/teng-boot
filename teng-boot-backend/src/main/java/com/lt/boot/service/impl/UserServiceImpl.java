package com.lt.boot.service.impl;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.page.PageVO;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.exception.BusinessException;
import com.lt.boot.exception.ThrowUtils;
import com.lt.boot.mapper.UserMapper;
import com.lt.boot.model.dto.user.*;
import com.lt.boot.model.entity.User;
import com.lt.boot.model.enums.user.UserGenderEnum;
import com.lt.boot.model.enums.user.UserStatusEnum;
import com.lt.boot.model.vo.user.UserVO;
import com.lt.boot.service.RbacService;
import com.lt.boot.service.UserService;
import com.lt.boot.utils.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RbacService rbacService;
    @Resource(name = "captchaService")
    private CaptchaService captchaService;

    @Override
    public UserVO userLogin(UserLoginDTO userLoginDTO) {
        // 1. AJ-Captcha 滑块验证码校验
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaType("blockPuzzle");
        captchaVO.setCaptchaVerification(userLoginDTO.getCaptchaVerification());
        ResponseModel captchaResponse = captchaService.check(captchaVO);
        if (!captchaResponse.isSuccess()) {
            log.info("滑块验证码校验失败，验证码数据：{}", userLoginDTO.getCaptchaVerification());
            throw new BusinessException(ErrorCode.INVALID_VERIFY_CODE, "验证码校验失败");
        }
        // 2. 查询用户
        String username = userLoginDTO.getUsername();
        String inputPassword = userLoginDTO.getUserPassword();
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            log.info("用户登录失败，用户不存在：{}", username);
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "用户名或密码错误");
        }
        // 3. 密码校验 — 优先 BCrypt，兼容旧 MD5
        String storedPassword = user.getUserPassword();
        boolean passwordMatch;
        if (isLegacyMd5(storedPassword)) {
            String md5Input = DigestUtils.md5DigestAsHex((UserConstant.SALT + inputPassword).getBytes(StandardCharsets.UTF_8));
            passwordMatch = md5Input.equals(storedPassword);
            if (passwordMatch) {
                user.setUserPassword(passwordEncoder.encode(inputPassword));
                updateById(user);
                log.info("用户 {} 密码已从 MD5 升级为 BCrypt", username);
            }
        } else {
            passwordMatch = passwordEncoder.matches(inputPassword, storedPassword);
        }
        if (!passwordMatch) {
            log.info("用户登录失败，密码错误：{}", username);
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "用户名或密码错误");
        }
        // 4. 状态校验
        if (user.getStatus() == UserStatusEnum.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKOUT);
        }
        // 5. 构建 UserVO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 6. 查询 RBAC 角色和权限
        List<String> roles = rbacService.getUserRoleKeys(user.getId());
        List<String> permissions = rbacService.getUserPermissions(user.getId());
        userVO.setRoles(roles);
        userVO.setPermissions(permissions);
        // 7. 生成 token
        String token = JwtUtils.getToken(user.getId());
        userVO.setToken(token);
        return userVO;
    }

    @Override
    @Transactional
    public void userRegister(UserRegisterDTO userRegisterDTO) {
        String username = userRegisterDTO.getUsername();
        String userPassword = userRegisterDTO.getUserPassword();
        RLock lock = redissonClient.getLock("user:register:" + username);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(0, -1, TimeUnit.MILLISECONDS);
            if (isLocked) {
                Long count = lambdaQuery().eq(User::getUsername, username).count();
                if (count > 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该用户已存在，请更改用户名");
                }
                User user = new User();
                user.setUsername(username);
                user.setUserPassword(passwordEncoder.encode(userPassword));
                user.setUserRole(UserConstant.DEFAULT_ROLE);
                user.setUserAvatar(UserConstant.DEFAULT_USER_AVATAR);
                boolean result = save(user);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
                // 分配默认角色
                rbacService.assignDefaultRole(user.getId());
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复注册");
            }
        } catch (InterruptedException e) {
            log.error("userRegister error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = UserThreadLocalUtils.getUserId();
        if (userId == null) {
            return new UserVO();
        }
        User user = getById(userId);
        if (user == null) {
            return new UserVO();
        }
        if (user.getStatus() == UserStatusEnum.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKOUT);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 查询 RBAC 角色和权限
        List<String> roles = rbacService.getUserRoleKeys(user.getId());
        List<String> permissions = rbacService.getUserPermissions(user.getId());
        userVO.setRoles(roles);
        userVO.setPermissions(permissions);
        return userVO;
    }

    @Override
    @Transactional
    public void addUser(UserAddDTO userAddDTO) {
        String username = userAddDTO.getUsername();
        Long count = lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号重复，请修改账号名称");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddDTO, user);
        user.setUserAvatar(UserConstant.DEFAULT_USER_AVATAR);
        user.setUserPassword(passwordEncoder.encode(UserConstant.DEFAULT_USER_PASSWORD));
        boolean result = save(user);
        if (!result) throw new BusinessException(ErrorCode.DB_SAVE_EXCEPTION);
        // 分配角色
        String roleKey = userAddDTO.getUserRole();
        if (StringUtils.isNotBlank(roleKey)) {
            rbacService.assignRole(user.getId(), roleKey);
        } else {
            rbacService.assignDefaultRole(user.getId());
        }
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        Long count = lambdaQuery()
                .ne(User::getId, userUpdateDTO.getId())
                .eq(User::getUsername, userUpdateDTO.getUsername())
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号重复，请修改账号名称");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        boolean result = updateById(user);
        if (!result) throw new BusinessException(ErrorCode.DB_UPDATE_EXCEPTION);
    }

    @Override
    public PageVO<User> listUserByPage(UserQuery userQuery) {
        Long id = userQuery.getId();
        String username = userQuery.getUsername();
        String userPhone = userQuery.getUserPhone();
        String userRealName = userQuery.getUserRealName();
        UserGenderEnum userGender = userQuery.getUserGender();
        Integer userAge = userQuery.getUserAge();
        String userEmail = userQuery.getUserEmail();
        String userProfile = userQuery.getUserProfile();
        UserStatusEnum status = userQuery.getStatus();
        String sortBy = userQuery.getSortBy();
        Boolean isAsc = userQuery.getIsAsc();
        Page<User> page = lambdaQuery()
                .eq(id != null, User::getId, id)
                .eq(userGender != null, User::getUserGender, userGender)
                .eq(userAge != null, User::getUserAge, userAge)
                .eq(status != null, User::getStatus, status)
                .like(StringUtils.isNotBlank(username), User::getUsername, username)
                .like(StringUtils.isNotBlank(userPhone), User::getUserPhone, userPhone)
                .like(StringUtils.isNotBlank(userRealName), User::getUserRealName, userRealName)
                .like(StringUtils.isNotBlank(userEmail), User::getUserEmail, userEmail)
                .like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .page(userQuery.toMpPageDefaultSortByCreateTimeDesc());
        if (SqlUtils.validSortField(sortBy)) {
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        }
        List<User> userList = page.getRecords();
        if (CollUtils.isEmpty(userList)) {
            return PageVO.empty(page);
        }
        return PageVO.of(page, userList);
    }

    @Override
    public PageVO<UserVO> listUserVOByPage(UserVOQuery userVOQuery) {
        Long id = userVOQuery.getId();
        String username = userVOQuery.getUsername();
        String userPhone = userVOQuery.getUserPhone();
        String userRealName = userVOQuery.getUserRealName();
        UserGenderEnum userGender = userVOQuery.getUserGender();
        Integer userAge = userVOQuery.getUserAge();
        String userEmail = userVOQuery.getUserEmail();
        String userProfile = userVOQuery.getUserProfile();
        Boolean isAsc = userVOQuery.getIsAsc();
        String sortBy = userVOQuery.getSortBy();
        Page<User> page = lambdaQuery()
                .eq(id != null, User::getId, id)
                .eq(userGender != null, User::getUserGender, userGender)
                .eq(userAge != null, User::getUserAge, userAge)
                .like(StringUtils.isNotBlank(username), User::getUsername, username)
                .like(StringUtils.isNotBlank(userPhone), User::getUserPhone, userPhone)
                .like(StringUtils.isNotBlank(userRealName), User::getUserRealName, userRealName)
                .like(StringUtils.isNotBlank(userEmail), User::getUserEmail, userEmail)
                .like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .page(userVOQuery.toMpPageDefaultSortByCreateTimeDesc());
        if (SqlUtils.validSortField(sortBy)) {
            page.addOrder(new OrderItem().setColumn(sortBy).setAsc(isAsc));
        }
        List<User> userList = page.getRecords();
        if (CollUtils.isEmpty(userList)) {
            return PageVO.empty(page);
        }
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).toList();
        return PageVO.of(page, userVOList);
    }

    @Override
    public void updateMyUser(UserUpdateMyDTO userUpdateMyDTO) {
        Long userId = UserThreadLocalUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long count = lambdaQuery()
                .ne(User::getId, userId)
                .eq(User::getUsername, userUpdateMyDTO.getUsername())
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号重复，请修改账号名称");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyDTO, user);
        user.setId(userId);
        boolean result = updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.DB_UPDATE_EXCEPTION);
    }

    @Override
    public void updateUserPwd(Long id, UserUpdatePwdDTO userUpdateDTO) {
        User user = new User();
        user.setId(id);
        user.setUserPassword(passwordEncoder.encode(userUpdateDTO.getUserPassword()));
        boolean result = updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void updateUserMyPwd(UserUpdateMyPwdDTO userUpdateMyPwdDTO) {
        Long userId = UserThreadLocalUtils.getUserId();
        String oldPassword = userUpdateMyPwdDTO.getOldPassword();
        String newPassword = userUpdateMyPwdDTO.getUserPassword();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
        String storedPassword = user.getUserPassword();
        boolean oldPwdMatch;
        if (isLegacyMd5(storedPassword)) {
            String md5Old = DigestUtils.md5DigestAsHex((UserConstant.SALT + oldPassword).getBytes(StandardCharsets.UTF_8));
            oldPwdMatch = md5Old.equals(storedPassword);
        } else {
            oldPwdMatch = passwordEncoder.matches(oldPassword, storedPassword);
        }
        if (!oldPwdMatch) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户原始密码错误");
        }
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUserPassword(passwordEncoder.encode(newPassword));
        boolean result = updateById(newUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    private boolean isLegacyMd5(String password) {
        return !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$");
    }
}
