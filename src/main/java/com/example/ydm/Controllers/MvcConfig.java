package com.example.ydm.Controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Зв'язуємо віртуальний шлях імеджес з реальною папкою на диску
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/C:/Users/DARINA/Desktop/кипфт программы/ООП/ПРАКТИКА 3 курс 2026 ООП/ydm/ydm/src/main/resources/static/images/");
    }
}