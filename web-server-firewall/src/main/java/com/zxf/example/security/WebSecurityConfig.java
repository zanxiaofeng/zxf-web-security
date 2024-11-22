package com.zxf.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .anyRequest().anonymous()
                .and()
                .csrf().disable()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "PUT"));
        // Override the httpFirewall in org.springframework.security.web.FilterChainProxy
        return (web) -> web.debug(true).httpFirewall(strictHttpFirewall);
    }
}
