package com.example.ByteForge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    public static final Double CPU_WALL_TIME = 20.0;

    public boolean isDevProfile() {
        return profile.equals("dev");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
}