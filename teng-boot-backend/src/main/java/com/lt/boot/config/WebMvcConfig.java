package com.lt.boot.config;

import com.lt.boot.aop.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * MVC Config
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor) //添加拦截器
                .addPathPatterns("/**"); //拦截所有请求
    }

    // XSS 过滤器暂时禁用，待后续调试
    // @Bean
    // public FilterRegistrationBean<Filter> xssFilterRegistration() {
    //     FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(new XssFilter());
    //     registration.addUrlPatterns("/*");
    //     registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    //     return registration;
    // }
}
