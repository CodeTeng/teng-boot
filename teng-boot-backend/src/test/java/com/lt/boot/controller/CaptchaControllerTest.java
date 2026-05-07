package com.lt.boot.controller;

import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.lt.boot.config.TestBeansConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CaptchaController 集成测试
 * <p>
 * 测试 AJ-Captcha 替换后的验证码接口
 */
@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
@Import(TestBeansConfig.class)
@DisplayName("CaptchaController 集成测试")
class CaptchaControllerTest {

    @Autowired(required = false)
    private CaptchaService captchaService;

    @Autowired(required = false)
    private CaptchaController captchaController;

    @Test
    @DisplayName("验证 CaptchaService Bean 存在")
    void testCaptchaServiceBeanExists() {
        assertNotNull(captchaService, "CaptchaService Bean 应该存在");
    }

    @Test
    @DisplayName("验证 CaptchaController Bean 存在")
    void testCaptchaControllerBeanExists() {
        assertNotNull(captchaController, "CaptchaController Bean 应该存在");
    }

    @Test
    @DisplayName("验证 CaptchaService.get 可调用")
    void testCaptchaServiceGet() {
        assertNotNull(captchaService);
        CaptchaVO request = new CaptchaVO();
        request.setCaptchaType("blockPuzzle");
        var response = captchaService.get(request);
        assertNotNull(response);
    }
}
