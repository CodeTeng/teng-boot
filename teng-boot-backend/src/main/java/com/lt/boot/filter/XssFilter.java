package com.lt.boot.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * XSS 过滤过滤器
 */
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        XssHttpServletRequestWrapper xssRequestWrapper = new XssHttpServletRequestWrapper(httpServletRequest);
        chain.doFilter(xssRequestWrapper, response);
    }
}
