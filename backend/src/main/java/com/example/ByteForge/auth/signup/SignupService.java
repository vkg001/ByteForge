package com.example.ByteForge.auth.signup;

import com.example.ByteForge.auth.otp.OtpDto;
import com.example.ByteForge.auth.otp.OtpService;
import com.example.ByteForge.auth.otp.VerifyOtpDto;
import com.example.ByteForge.auth.signup.exceptions.UserAlreadyExistsException;
import com.example.ByteForge.config.Constants;
import com.example.ByteForge.auth.AuthResponse;
import com.example.ByteForge.config.jwt.JwtService;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.enums.UserRole;
import com.example.ByteForge.user.stats.events.UserRegisteredEvent;
import com.example.ByteForge.user.core.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignupService {
    private static final String SIGNUP_OTP_KEY = "signup-otp:";
    private static final String SIGNUP_USER_DETAILS_KEY = "signup-user-details:";

    private final ObjectMapper objectMapper;
    private final UsersRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    Optional<OtpDto> sendOtp(SignupDto user) {
        boolean res = repository.existsByEmail(user.getEmail());
        if (res) throw new UserAlreadyExistsException();

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

        throw new RuntimeException();
    }

    @Transactional
    AuthResponse verifyOtp(VerifyOtpDto data) {
        var res = otpService.verifyOtp(data.getOtp(), data.getEmail(), SIGNUP_OTP_KEY);
        if (res.isPresent()  &&  res.get()) {
            String userDetailsJson = redisTemplate.opsForValue().get(SIGNUP_USER_DETAILS_KEY + data.getEmail());
            if (userDetailsJson == null) throw new RuntimeException();

            SignupDto userData = objectMapper.readValue(userDetailsJson, SignupDto.class);
            UserEntity user = new UserEntity(userData);
            user.setUserRole(UserRole.USER);

            if (repository.existsByEmail(user.getEmail())) throw new UserAlreadyExistsException();
            repository.save(user);
            eventPublisher.publishEvent(new UserRegisteredEvent(user.getId()));
            String jwt = jwtService.generateToken(user.getEmail());
            return new AuthResponse(jwt);
        }

        throw new RuntimeException();
    }
}
