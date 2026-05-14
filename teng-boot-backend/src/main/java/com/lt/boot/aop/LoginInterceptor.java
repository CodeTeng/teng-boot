package com.lt.boot.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.common.ResultUtils;
import com.lt.boot.exception.BusinessException;
import com.lt.boot.utils.JwtUtils;
import com.lt.boot.utils.UserThreadLocalUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2024/1/27 16:00
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final Set<String> urls = new HashSet<>(Arrays.asList(
            "/api/users/login", "/api/users/list/page/vo", "/api/users/register",
            "/api/users/findPassword", "/api/druid/index.html", "/api/captcha",
            "/api/captcha/get", "/api/captcha/check",
            "/api/swagger-ui.html", "/api/swagger-resources", "/api/webjars", "/api/doc.html",
            "/api/v3/api-docs", "/api/v3/api-docs/swagger-config", "/api/v3/api-docs/default"));

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        if (urls.contains(requestURI)) {
            // 直接放行
            return true;
        }
        Method method = handlerMethod.getMethod();
        String methodName = method.getName();
        log.info("====拦截到了方法：{}，在该方法执行之前执行====", methodName);
        String token = request.getHeader("Authorization");
        log.info("token 获取为：{}", token);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        // 获取 payload 信息
        Claims claimsBody = JwtUtils.getClaimsBody(token);
        if (claimsBody == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        // 判断是否过期
        int res = JwtUtils.verifyToken(claimsBody);
        if (res > 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户登录已过期，请重新登录");
        }
        // 从 subject 获取用户ID
        String subject = claimsBody.getSubject();
        if (StringUtils.isBlank(subject)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无效的登录凭证");
        }
        Long userId = Long.valueOf(subject);
        log.info("token 解析成功，userId = {}", userId);

        // 检查 Redis 黑名单，判断是否被强制下线
        String banKey = "token:ban:" + userId;
        String banTimeStr = stringRedisTemplate.opsForValue().get(banKey);
        if (StringUtils.isNotBlank(banTimeStr)) {
            long banTime = Long.parseLong(banTimeStr);
            Date issuedAt = claimsBody.getIssuedAt();
            if (issuedAt != null && issuedAt.getTime() < banTime) {
                // Token 在禁用前签发，已失效
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                        ResultUtils.error(ErrorCode.ACCOUNT_LOCKOUT, "账号已被禁用，请重新登录")));
                return false;
            }
        }

        UserThreadLocalUtils.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserThreadLocalUtils.clear();
    }
}
