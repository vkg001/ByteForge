package com.example.ByteForge.auth.signup;

import com.example.ByteForge.auth.otp.OtpDto;
import com.example.ByteForge.auth.otp.OtpService;
import com.example.ByteForge.auth.otp.VerifyOtpDto;
import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.config.Constants;
import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SignupService {
    private static final String SIGNUP_OTP_KEY = "signup-otp:";
    private static final String SIGNUP_USER_DETAILS_KEY = "signup-user-details:";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private SignupRepository repository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    Optional<Object> sendOtp(SignupDto user) {
        boolean res = repository.existsByEmail(user.getEmail());
        if (res) return Optional.of(new SimpleMessageDto("Email already exists!!", HttpStatus.CONFLICT));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (otpService.sendOtp(user.getEmail(), SIGNUP_OTP_KEY)) {
            String userDetails = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(
                    SIGNUP_USER_DETAILS_KEY + user.getEmail(),
                    userDetails,
                    Constants.OTP_VALID_MINUTES + 1,
                    TimeUnit.MINUTES
            );

            return Optional.of(new OtpDto(Constants.OTP_VALID_MINUTES, "OTP has been sent for signup."));
        }

        return Optional.of(new SimpleMessageDto("OTP could not be sent at the moment.", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    Object verifyOtp(VerifyOtpDto data) {
        var res = otpService.verifyOtp(data.getOtp(), data.getEmail(), SIGNUP_OTP_KEY);
        if (res.isPresent()  &&  res.get() instanceof Boolean  &&  (Boolean) res.get()) {
            String userDetailsJson = redisTemplate.opsForValue().get(SIGNUP_USER_DETAILS_KEY + data.getEmail());
            if (userDetailsJson == null) return new SimpleMessageDto("Something went wrong !!", HttpStatus.BAD_REQUEST);

            SignupDto userData = objectMapper.readValue(userDetailsJson, SignupDto.class);
            SignupEntity user = new SignupEntity(userData);

            if (repository.existsByEmail(user.getEmail())) return new SimpleMessageDto("User already exist with the same email !!", HttpStatus.FORBIDDEN);
            repository.save(user);
            String jwt = jwtService.generateToken(user.getEmail());
            return new AuthResponse(jwt);
        }

        return new SimpleMessageDto((res.isPresent()  &&  res.get() instanceof String) ? (String)(res.get()) : "Invalid OTP", HttpStatus.FORBIDDEN);
    }
}
