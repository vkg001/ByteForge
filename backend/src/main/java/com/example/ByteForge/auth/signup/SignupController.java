package com.example.ByteForge.auth.signup;

import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.auth.otp.OtpDto;
import com.example.ByteForge.auth.otp.VerifyOtpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SignupController {
    @Autowired
    private SignupService signupService;

    @PostMapping("/signup-init")
    ResponseEntity<OtpDto> signup(@RequestBody SignupDto user) {
        var res = signupService.sendOtp(user);
        if (res.isPresent()) {
            var response = res.get();
            return ResponseEntity.ok(response);
        }

        throw new RuntimeException();
    }

    @PostMapping("/signup-complete")
    ResponseEntity<AuthResponse> verifyOtp(@RequestBody VerifyOtpDto data) {
        return ResponseEntity.ok(signupService.verifyOtp(data));
    }
}
