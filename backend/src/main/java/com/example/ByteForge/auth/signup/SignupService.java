package com.example.ByteForge.auth.signup;

import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
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

    Optional<Object> sendOtp(SignupDto user) {
        boolean res = repository.existsByEmail(user.getEmail());
        int otp = ((int)(Math.random() * 1000000));

        log.warn("Generated OTP: {}", otp);

        String redisKey = SIGNUP_OTP_KEY + user.getEmail();
        redisTemplate.opsForValue().set(
                redisKey,
                String.valueOf(otp),
                Constants.OTP_VALID_MINUTES,
                TimeUnit.MINUTES
        );

        String userDetails = objectMapper.writeValueAsString(user);
        redisTemplate.opsForValue().set(
                SIGNUP_USER_DETAILS_KEY + user.getEmail(),
                userDetails,
                Constants.OTP_VALID_MINUTES + 1,
                TimeUnit.MINUTES
        );

        if (res) return Optional.of(new SimpleMessageDto("Email already exists!!", HttpStatus.CONFLICT));
        return Optional.of(new OtpDto(Constants.OTP_VALID_MINUTES, "OTP has been sent for signup."));
    }

    Object verifyOtp(VerifyOtp data) {
        String org_otp = redisTemplate.opsForValue().get(SIGNUP_OTP_KEY + data.getEmail());
        if (org_otp == null) return new SimpleMessageDto("OTP Expired", HttpStatus.FORBIDDEN);

        String userDetailsJson = redisTemplate.opsForValue().get(SIGNUP_USER_DETAILS_KEY + data.getEmail());
        if (userDetailsJson == null) return new SimpleMessageDto("Something went wrong !!", HttpStatus.BAD_REQUEST);

        SignupDto userData = objectMapper.readValue(userDetailsJson, SignupDto.class);
        SignupEntity user = new SignupEntity(userData);

        if (org_otp.equals(data.getOtp())) {
            repository.save(user);
            return true;
        }

        return new SimpleMessageDto("Invalid OTP", HttpStatus.FORBIDDEN);
    }
}
