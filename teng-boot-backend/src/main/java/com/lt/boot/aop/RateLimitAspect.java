package com.lt.boot.aop;

import com.lt.boot.annotation.RateLimit;
import com.lt.boot.common.ErrorCode;
import com.lt.boot.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 限流切面（基于 Redis + Lua 滑动窗口）
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local now = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local limit = tonumber(ARGV[3])\n" +
            "redis.call('ZREMRANGEBYSCORE', key, 0, now - window)\n" +
            "local count = redis.call('ZCARD', key)\n" +
            "if count < limit then\n" +
            "    redis.call('ZADD', key, now, now .. '_' .. math.random())\n" +
            "    redis.call('EXPIRE', key, window)\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        // 构建 Redis key
        String key = rateLimit.key();
        if (key.isBlank()) {
            key = "rate_limit:" + className + ":" + methodName;
        } else {
            key = "rate_limit:" + key;
        }

        int limit = rateLimit.limit();
        int seconds = rateLimit.seconds();
        long now = System.currentTimeMillis();

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        List<String> keys = Arrays.asList(key);
        Long result = stringRedisTemplate.execute(redisScript, keys, String.valueOf(now), String.valueOf(seconds * 1000), String.valueOf(limit));

        if (result == null || result == 0) {
            log.warn("请求被限流，key={}, limit={}, seconds={}", key, limit, seconds);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        return joinPoint.proceed();
    }
}
