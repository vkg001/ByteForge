package com.example.ByteForge.auth.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Getter
@Setter
public class OtpDto {
    private int validForMinutes;
    private String description;

    public String toString () {
        return "[validForMinutes: " + validForMinutes + ", description: " + description + "]";
    }
}

@Getter
@Setter
@AllArgsConstructor
class VerifyOtp {
    private String otp;
    private String email;
}
