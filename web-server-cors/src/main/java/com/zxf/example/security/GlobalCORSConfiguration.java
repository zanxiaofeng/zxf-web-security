package com.zxf.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class GlobalCORSConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/my/**")
                        .allowedOrigins("https://localhost:8082")
                        .allowedMethods("GET", "POST", "PUT")
                        //.allowedHeaders("")
                        //.exposedHeaders("")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
