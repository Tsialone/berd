package com.berd.dev.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.berd.dev.interceptor.LastUrlInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LastUrlInterceptor lastUrlInterceptor;

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // On applique l'intercepteur partout sauf sur les fichiers statiques
        registry.addInterceptor(lastUrlInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/error" , "/assets/**");
    }
}
