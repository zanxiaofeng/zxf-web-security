package com.zxf.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests.anyRequest().anonymous()
        );
        http.headers(headers -> {
            headers.defaultsDisabled()
                    .cacheControl(Customizer.withDefaults())
                    .frameOptions(Customizer.withDefaults())
                    .contentTypeOptions(Customizer.withDefaults())
                    .xssProtection(Customizer.withDefaults())
                    .httpStrictTransportSecurity(Customizer.withDefaults())
                    .referrerPolicy(referrerPolicy -> referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                    .contentSecurityPolicy(contentSecurityPolicy -> {
                        contentSecurityPolicy.policyDirectives("default-src 'self'");
                    });

        });
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(true);
    }
}
