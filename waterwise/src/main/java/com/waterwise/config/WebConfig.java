package com.waterwise.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ProfileCompletionInterceptor profileCompletionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(profileCompletionInterceptor)
                .addPathPatterns("/admin/**") // Aplicar apenas a rotas administrativas
                .excludePathPatterns("/admin/api/**"); // Excluir APIs se necessário
    }
}