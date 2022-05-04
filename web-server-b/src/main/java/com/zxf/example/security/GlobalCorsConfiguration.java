package com.zxf.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class GlobalCorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/my/**")
                        .allowedOrigins("https://localhost:8081")
                        .allowedMethods("GET")
                        //.allowedHeaders("")
                        //.exposedHeaders("")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
