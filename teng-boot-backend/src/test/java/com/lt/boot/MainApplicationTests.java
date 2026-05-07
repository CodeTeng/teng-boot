package com.lt.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import jakarta.annotation.Resource;

@SpringBootTest
class MainApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        System.out.println(stringRedisTemplate);
    }
}
