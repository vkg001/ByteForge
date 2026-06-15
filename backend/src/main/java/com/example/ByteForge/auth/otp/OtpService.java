package com.example.ByteForge.auth.otp;

import com.example.ByteForge.auth.signup.exceptions.InvalidOtpException;
import com.example.ByteForge.config.Constants;
import com.example.ByteForge.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

        // logic to send on email
        emailService.sendEmail(email, "OTP -- ByteForge Coding", "OTP for verification: " + String.valueOf(otp) + ".<br>OTP is valid for 5 minutes only, if you have not requested the OTP kindly ignore and do not share the OTP with anyone.<br>Thanks,<br>ByteForge Team");

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
}
