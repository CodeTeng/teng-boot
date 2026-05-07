package com.lt.boot.service.impl;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.boot.constant.UserConstant;
import com.lt.boot.mapper.UserRoleMapper;
import com.lt.boot.model.dto.user.UserLoginDTO;
import com.lt.boot.model.dto.user.UserRegisterDTO;
import com.lt.boot.model.dto.user.UserUpdateMyPwdDTO;
import com.lt.boot.model.dto.user.UserUpdatePwdDTO;
import com.lt.boot.model.entity.User;
import com.lt.boot.model.entity.UserRole;
import com.lt.boot.service.RbacService;
import com.lt.boot.service.UserService;
import com.lt.boot.utils.UserThreadLocalUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Resource
    private RbacService rbacService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean(name = "captchaService")
    private CaptchaService captchaService;

    @AfterEach
    void tearDown() {
        UserThreadLocalUtils.clear();
    }

    @Test
    void testUserRegister_Success() throws Exception {
        // Mock Redisson 分布式锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 注册新用户
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser_" + System.currentTimeMillis());
        dto.setUserPassword("123456");
        dto.setCheckPassword("123456");
        userService.userRegister(dto);

        // 验证用户已创建
        User user = userService.lambdaQuery().eq(User::getUsername, dto.getUsername()).one();
        assertNotNull(user);

        // 验证密码是 BCrypt 格式
        assertTrue(user.getUserPassword().startsWith("$2a$"));

        // 验证默认角色已分配
        var userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId()));
        assertFalse(userRoles.isEmpty());

        // 验证 hasDefaultRole
        var roles = rbacService.getUserRoleKeys(user.getId());
        assertTrue(roles.contains("user"));
    }

    @Test
    void testUserRegister_DuplicateUsername() throws Exception {
        // Mock Redisson 分布式锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 先注册一个用户
        String username = "duplicate_" + System.currentTimeMillis();
        UserRegisterDTO dto1 = new UserRegisterDTO();
        dto1.setUsername(username);
        dto1.setUserPassword("123456");
        dto1.setCheckPassword("123456");
        userService.userRegister(dto1);

        // 再注册相同用户名应抛出异常
        UserRegisterDTO dto2 = new UserRegisterDTO();
        dto2.setUsername(username);
        dto2.setUserPassword("123456");
        dto2.setCheckPassword("123456");

        assertThrows(com.lt.boot.exception.BusinessException.class, () -> {
            userService.userRegister(dto2);
        });
    }

    @Test
    void testGetCurrentUser_WithoutLogin() {
        // 未登录时返回空 UserVO
        var userVO = userService.getCurrentUser();
        assertNotNull(userVO);
        assertNull(userVO.getId());
    }

    @Test
    void testGetCurrentUser_WithLogin() throws Exception {
        // Mock Redisson 锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 先注册一个用户
        String username = "currentuser_" + System.currentTimeMillis();
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setUserPassword("123456");
        dto.setCheckPassword("123456");
        userService.userRegister(dto);

        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        assertNotNull(user);

        // 设置 ThreadLocal
        UserThreadLocalUtils.setUserId(user.getId());

        var userVO = userService.getCurrentUser();
        assertNotNull(userVO);
        assertEquals(user.getUsername(), userVO.getUsername());
        assertNotNull(userVO.getRoles());
        assertTrue(userVO.getRoles().contains("user"));
    }

    @Test
    void testUserLogin_Success_BCrypt() throws Exception {
        // Mock Redisson 锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 先注册用户
        String username = "logintest_" + System.currentTimeMillis();
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setUserPassword("password123");
        registerDTO.setCheckPassword("password123");
        userService.userRegister(registerDTO);

        // Mock AJ-Captcha 验证码校验成功
        ResponseModel mockCaptchaResponse = mock(ResponseModel.class);
        when(mockCaptchaResponse.isSuccess()).thenReturn(true);
        when(captchaService.check(any(CaptchaVO.class))).thenReturn(mockCaptchaResponse);

        // 执行登录
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setUserPassword("password123");
        loginDTO.setCaptchaVerification("mock-captcha-verification-data");

        var userVO = userService.userLogin(loginDTO);
        assertNotNull(userVO);
        assertEquals(username, userVO.getUsername());
        assertNotNull(userVO.getToken());
    }

    @Test
    void testUserLogin_WrongPassword() {
        // Mock AJ-Captcha 验证码校验成功
        ResponseModel mockCaptchaResponse = mock(ResponseModel.class);
        when(mockCaptchaResponse.isSuccess()).thenReturn(true);
        when(captchaService.check(any(CaptchaVO.class))).thenReturn(mockCaptchaResponse);

        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("nonexistent_user");
        loginDTO.setUserPassword("wrongpassword");
        loginDTO.setCaptchaVerification("mock-captcha-verification-data");

        assertThrows(com.lt.boot.exception.BusinessException.class, () -> {
            userService.userLogin(loginDTO);
        });
    }

    @Test
    void testUpdateUserPwd() throws Exception {
        // Mock Redisson 锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 注册用户
        String username = "pwdupdate_" + System.currentTimeMillis();
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setUserPassword("123456");
        dto.setCheckPassword("123456");
        userService.userRegister(dto);

        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        assertNotNull(user);
        Long userId = user.getId();

        // 修改密码（后台管理）
        UserUpdatePwdDTO pwdDTO = new UserUpdatePwdDTO();
        pwdDTO.setUserPassword("newPassword789");
        userService.updateUserPwd(userId, pwdDTO);

        // 验证密码已更新
        User updatedUser = userService.getById(userId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("newPassword789", updatedUser.getUserPassword()));
    }

    @Test
    void testUserPasswordIsBCrypt() throws Exception {
        // Mock Redisson 锁
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doReturn(true).when(mockLock).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // 注册用户并验证密码是 BCrypt 格式
        String username = "bcrypttest_" + System.currentTimeMillis();
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setUserPassword("password123");
        dto.setCheckPassword("password123");
        userService.userRegister(dto);

        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        assertNotNull(user);
        assertTrue(user.getUserPassword().startsWith("$2a$"),
                "密码应以 BCrypt 格式存储，但实际为: " + user.getUserPassword());
    }
}
