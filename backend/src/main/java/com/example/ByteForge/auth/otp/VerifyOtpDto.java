package com.example.ByteForge.auth.otp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyOtpDto {
    private String otp;
    private String email;
}
