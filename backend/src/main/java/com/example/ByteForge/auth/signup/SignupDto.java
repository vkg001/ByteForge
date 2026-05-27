package com.example.ByteForge.auth.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupDto {
    private String name;
    private String email;
    private String password;

    public String toString() {
        return name + ", " + email + ", " + password;
    }
}
