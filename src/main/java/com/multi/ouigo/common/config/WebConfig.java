package com.multi.ouigo.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 프로필 사진 기능을 위해 구현

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("{FILE_HANDLER}")
    private String fileHandler;
    @Value("{FILE_PATH}")
    private String filePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(fileHandler)
            .addResourceLocations(filePath);
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/");
    }
}

