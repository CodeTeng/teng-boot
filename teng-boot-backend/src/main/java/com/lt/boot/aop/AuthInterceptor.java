package com.lt.boot.aop;

import com.lt.boot.annotation.AuthCheck;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.exception.BusinessException;
import com.lt.boot.model.enums.user.UserStatusEnum;
import com.lt.boot.model.vo.user.UserVO;
import com.lt.boot.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    @Around("@within(com.lt.boot.annotation.AuthCheck) || @annotation(com.lt.boot.annotation.AuthCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 从方法或类上获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuthCheck authCheck = signature.getMethod().getAnnotation(AuthCheck.class);
        if (authCheck == null) {
            authCheck = joinPoint.getTarget().getClass().getAnnotation(AuthCheck.class);
        }
        if (authCheck == null) {
            return joinPoint.proceed();
        }
        String mustRole = authCheck.mustRole();
        if (StringUtils.isBlank(mustRole)) {
            return joinPoint.proceed();
        }
        UserVO currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (currentUser.getStatus() == UserStatusEnum.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKOUT);
        }
        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains(mustRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
