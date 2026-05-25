package com.example.ByteForge.auth.signup;

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

    @PostMapping("/signup")
    ResponseEntity<Object> signup(@RequestBody SignupDto user) {
        var res = signupService.sendOtp(user);
        if (res.isPresent()) {
            var response = res.get();
            return ResponseEntity.ok(response);
        }

        return new ResponseEntity<>(new SimpleMessageDto("Something went wrong !!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    ResponseEntity<Object> verifyOtp(@RequestBody VerifyOtp data) {
        var res = signupService.verifyOtp(data);
        if (res instanceof Boolean && (Boolean) res) {
            return ResponseEntity.ok("OTP Verified");
        }
        if (res instanceof SimpleMessageDto) return new ResponseEntity<>(res, ((SimpleMessageDto) res).getStatus());

        return new ResponseEntity<>("Something went wrong !!", HttpStatus.BAD_REQUEST);
    }
}
