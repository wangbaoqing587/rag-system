package com.wbq.ragsystem.config;

import com.wbq.ragsystem.admin.HelpCenterAdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class HelpCenterAdminAuthInterceptor implements HandlerInterceptor {

    private final HelpCenterAdminAuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (authService.validate(token)) {
                return true;
            }
        }
        log.warn("admin unauthorized path={} method={}", request.getRequestURI(), request.getMethod());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Unauthorized\"}");
        return false;
    }
}
