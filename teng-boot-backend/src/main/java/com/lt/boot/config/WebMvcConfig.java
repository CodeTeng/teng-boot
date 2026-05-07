package com.lt.boot.config;

import com.lt.boot.aop.LoginInterceptor;
import com.lt.boot.filter.XssFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * MVC Config
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
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

    /**
     * 登录拦截器
     */
    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()) //添加拦截器
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
