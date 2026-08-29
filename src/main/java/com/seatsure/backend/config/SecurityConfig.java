package com.seatsure.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Tells Spring: "Read this file during boot-up for special rules"
public class SecurityConfig {

    // 1. We create the BCrypt tool and register it as a Bean in the Spring Factory
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. We tell the Vault to keep its doors open for Postman tests (for now)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for REST APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all traffic temporarily so we can test
                );
        return http.build();
    }
}