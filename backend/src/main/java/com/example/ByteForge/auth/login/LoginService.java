package com.example.ByteForge.auth.login;

import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.user.core.repository.UsersRepository;
import com.example.ByteForge.config.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private UsersRepository repository;

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
