package com.lt.boot.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.lt.boot.common.BaseResponse;
import com.lt.boot.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * AJ-Captcha 验证码控制器
 * <p>
 * 提供滑块验证码的获取和校验接口，
 * 底层使用 AJ-Captcha 行为验证码引擎。
 * <p>
 * 获取验证码：POST /api/captcha/get
 * 校验验证码：POST /api/captcha/check
 */
@Tag(name = "验证码模块")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource(name = "captchaService")
    private CaptchaService captchaService;

    @PostMapping("/get")
    @Operation(summary = "获取滑块验证码")
    public BaseResponse<Object> getCaptcha(@RequestBody CaptchaVO captchaVO) {
        ResponseModel response = captchaService.get(captchaVO);
        if (!response.isSuccess()) {
            return ResultUtils.error(com.lt.boot.common.ErrorCode.SYSTEM_ERROR, "获取验证码失败");
        }
        return ResultUtils.success(response.getRepData());
    }

    @PostMapping("/check")
    @Operation(summary = "校验滑块验证码")
    public BaseResponse<Boolean> checkCaptcha(@RequestBody CaptchaVO captchaVO) {
        ResponseModel response = captchaService.check(captchaVO);
        return ResultUtils.success(response.isSuccess());
    }
}
