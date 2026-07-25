package com.example.ByteForge.config.rate_limit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.redis.redisson.Bucket4jRedisson;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
public class RateLimitingAspect {
    private final RedissonBasedProxyManager<String> proxyManager;

    public RateLimitingAspect(RedissonClient redissonClient) {
        CommandAsyncExecutor commandExecutor = ((Redisson) redissonClient).getCommandExecutor();
        this.proxyManager = Bucket4jRedisson.casBasedBuilder(commandExecutor).build();
    }

    /**
     * Intercepts any method annotated with @RateLimit.
     */
    @Around("@annotation(rateLimitAnnotation)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimitAnnotation) throws Throwable {

        // 1. Identify the principal (User ID or IP Address)
        String principalId = resolvePrincipal();

        // 2. Construct a unique Redis key for this user and this specific action
        // Example: "rate_limit:code_submission:user_123"
        String bucketKey = "rate_limit:" + rateLimitAnnotation.key() + ":" + principalId;

        // 3. Define the bucket configuration based on the annotation properties
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(
                        rateLimitAnnotation.capacity(),
                        Refill.intervally(
                                rateLimitAnnotation.refillTokens(),
                                Duration.ofSeconds(rateLimitAnnotation.refillDurationInSeconds())
                        )
                ))
                .build();

        // 4. Retrieve or create the bucket in Redis
        // getProxy() handles the concurrency; it ensures atomic creation if it doesn't exist.
        Bucket bucket = proxyManager.builder().build(bucketKey, configuration);

        // 5. Try to consume a token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Token consumed successfully. Proceed with the actual method execution.
            // You can optionally add headers to the response indicating remaining tokens here,
            // but for an MVP, just letting it proceed is sufficient.
            return joinPoint.proceed();
        } else {
            // No tokens left. The limit is exceeded.
            // Calculate how long the user must wait.
            long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;

            // Throw a custom exception. We will catch this globally.
            throw new RateLimitExceededException("Too many requests. Please try again in " + waitForRefillSeconds + " seconds.");
        }
    }

    /**
     * Determines who is making the request.
     * Prefers the authenticated User ID from Spring Security.
     * Falls back to IP Address for unauthenticated endpoints.
     */
    private String resolvePrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            // Assuming your JWT filter sets the username or user ID as the principal name.
            return authentication.getName();
        }

        // Fallback: If not authenticated, use IP Address.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return getClientIP(request);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0]; // Handle multiple proxies
    }
}
