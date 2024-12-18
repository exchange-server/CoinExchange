package com.bizzan.bitrade.config;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String contextPath = env.getProperty("management.context-path");
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = "";
        }
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**" + contextPath + "/**").authenticated().anyRequest().permitAll().and()
                .httpBasic();
    }
}