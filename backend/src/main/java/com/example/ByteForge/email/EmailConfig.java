package com.example.ByteForge.email;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String username;
}
