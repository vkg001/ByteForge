package com.example.ByteForge.auth.signup;

import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.auth.otp.VerifyOtpDto;
import com.example.ByteForge.common.SimpleMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SignupController {
    @Autowired
    private SignupService signupService;

    @PostMapping("/signup-init")
    ResponseEntity<Object> signup(@RequestBody SignupDto user) {
        var res = signupService.sendOtp(user);
        if (res.isPresent()) {
            var response = res.get();
            if (response instanceof SimpleMessageDto) {
                var x = ((SimpleMessageDto) response);
                return new ResponseEntity<>(x.getMessage(), x.getStatus());
            }
            return ResponseEntity.ok(response);
        }

        return new ResponseEntity<>(new SimpleMessageDto("Something went wrong !!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/signup-complete")
    ResponseEntity<Object> verifyOtp(@RequestBody VerifyOtpDto data) {
        var res = signupService.verifyOtp(data);
        if (res instanceof AuthResponse) {
            return ResponseEntity.ok(res);
        }
        if (res instanceof SimpleMessageDto) return new ResponseEntity<>(res, ((SimpleMessageDto) res).getStatus());

        return new ResponseEntity<>("Something went wrong !!", HttpStatus.BAD_REQUEST);
    }
}
