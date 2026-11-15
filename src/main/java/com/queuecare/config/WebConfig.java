package com.queuecare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FlashBersihInterceptor flashBersihInterceptor;

    public WebConfig(FlashBersihInterceptor flashBersihInterceptor) {
        this.flashBersihInterceptor = flashBersihInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(flashBersihInterceptor);
    }
}
