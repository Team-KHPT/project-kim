package com.khpt.projectkim.config;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String cfConnectingIp = request.getHeader("cf-connecting-ip");
        String ipAddress = (cfConnectingIp != null && !cfConnectingIp.isEmpty()) ? cfConnectingIp : request.getRemoteAddr();
        MDC.put("ip", ipAddress);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        MDC.remove("ip");
    }

}

