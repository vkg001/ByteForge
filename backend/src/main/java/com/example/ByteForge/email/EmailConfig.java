package com.example.ByteForge.email;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String username;
}
