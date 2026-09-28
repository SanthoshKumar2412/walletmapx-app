package com.walletmapx.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String uploadLocation = Paths
                .get(profileUploadDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry
                .addResourceHandler("/uploads/profile/**")
                .addResourceLocations(uploadLocation);
    }
}