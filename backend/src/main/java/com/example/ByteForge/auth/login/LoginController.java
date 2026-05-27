package com.example.ByteForge.auth.login;

import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.common.SimpleMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto user) {
        var loginResponse = loginService.login(user);
        if (loginResponse.isPresent()) {
            var lgr = loginResponse.get();

            if (lgr instanceof HttpStatus) {
                return new ResponseEntity<>("", (HttpStatus)lgr);
            } else if (lgr instanceof AuthResponse) {
                return ResponseEntity.ok(lgr);
            }
        }
        return new ResponseEntity<>("Something went wrong !!", HttpStatus.BAD_REQUEST);
    }
}
