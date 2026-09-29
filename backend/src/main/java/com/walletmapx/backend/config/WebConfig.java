package com.walletmapx.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadPath = Paths
                .get(profileUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler("/uploads/profile/**")
                .addResourceLocations(
                        uploadPath.toUri().toString()
                );
    }
}