package com.wbq.ragsystem.config;

import com.wbq.ragsystem.admin.HelpCenterAdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class HelpCenterAdminWebConfig implements WebMvcConfigurer {

    private final HelpCenterAdminAuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HelpCenterAdminAuthInterceptor(authService))
                .addPathPatterns("/help_center/admin/**")
                .excludePathPatterns("/help_center/admin/login");
    }
}
