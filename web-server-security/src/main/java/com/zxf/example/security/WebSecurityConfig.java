package com.zxf.example.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().anonymous()
                .and().headers()
                .defaultsDisabled()
                .cacheControl().and()
                .frameOptions().deny()
                .contentTypeOptions().and()
                .xssProtection().and()
                .frameOptions().and()
                .httpStrictTransportSecurity().and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN).and()
                .contentSecurityPolicy("default-src 'self'")
                .and();
    }
}
