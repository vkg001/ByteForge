package com.example.ByteForge.auth.login;

import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.auth.signup.SignupRepository;
import com.example.ByteForge.auth.signup.SignupService;
import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private SignupRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public Optional<?> login(LoginDto user) {
        var data = repository.findByEmail(user.getEmail());
        if (data == null) return Optional.of(HttpStatus.NOT_FOUND);
        if (passwordEncoder.matches(user.getPassword(), data.getPassword())) return Optional.of(new AuthResponse(jwtService.generateToken(user.getEmail())));
        return Optional.of(HttpStatus.UNAUTHORIZED);
    }
}
