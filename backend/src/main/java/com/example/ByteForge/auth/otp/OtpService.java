package com.example.ByteForge.auth.otp;

import com.example.ByteForge.auth.signup.exceptions.InvalidOtpException;
import com.example.ByteForge.config.Constants;
import com.example.ByteForge.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtpService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${spring.profiles.active}")
    private String profileUsed;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    public boolean sendOtp(String email, String saveKey) {
        int otp = ((int)(Math.random() * 1000000));

        if (profileUsed.equalsIgnoreCase("dev")) {
            log.warn("Generated OTP: {}", otp);
        }

        String redisKey = "SAVE_KEY:" + saveKey + ":EMAIL:" + email + ":";
        redisTemplate.opsForValue().set(
                redisKey,
                String.valueOf(otp),
                Constants.OTP_VALID_MINUTES,
                TimeUnit.MINUTES
        );

        emailService.sendEmail(email, "OTP -- ByteForge Coding", _buildOTPBody(otp, email));

        return true;
    }

    public Optional<Boolean> verifyOtp(String otp, String email, String saveKey) {
        String redisKey = "SAVE_KEY:" + saveKey + ":EMAIL:" + email + ":";
        String org_otp = redisTemplate.opsForValue().get(redisKey);
        if (org_otp == null) throw new InvalidOtpException("OTP Expired");
        if (otp.equals(org_otp)) {
            redisTemplate.opsForValue().getAndDelete(redisKey);
            return Optional.of(true);
        }

        throw new InvalidOtpException("Incorrect OTP");
    }


    private String _buildOTPBody(int otp, String userEmail) {
        Context context = new Context();
        context.setVariable("otpCode", otp);
        context.setVariable("expiryMinutes", 5);
        context.setVariable("appName", "ByteForge");
        context.setVariable("userEmail", userEmail);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
        context.setVariable("requestTime", LocalDateTime.now().format(formatter));
        context.setVariable("deviceInfo", "Mac OS / Chrome");
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("otp-email", context);
    }
}
