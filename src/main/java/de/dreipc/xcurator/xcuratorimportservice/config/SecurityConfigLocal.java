package de.dreipc.xcurator.xcuratorimportservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Config enables the Spring Security with JWT.
 */

@Configuration
@Profile("local") // Allow all endpoints on local development
public class SecurityConfigLocal {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Kubernetes Endpoints
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, ",/manage/metrics", "/manage/health/**")
                .permitAll();

        // GraphQL Endpoint
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/graphql")
                .permitAll();

        // Sessions are controlled by the Gateway || DO NOT CREATE ONE YOURSELF
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);

        // Security Settings
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .cors();

        return http.build();
    }
}