package com.app.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();

        // Serve uploaded files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // 1 hour cache

        System.out.println("Serving static files from: " + uploadPath);
    }
}
