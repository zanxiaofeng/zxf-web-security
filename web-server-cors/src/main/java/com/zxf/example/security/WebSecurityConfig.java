package com.zxf.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.management.MXBean;
import java.util.Arrays;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.debug(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().anonymous().and().csrf().disable();
    }

    // Override the httpFirewall in org.springframework.security.web.FilterChainProxy
    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall httpFirewall = new StrictHttpFirewall();
        httpFirewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "PUT"));
        return httpFirewall;
    }
}
