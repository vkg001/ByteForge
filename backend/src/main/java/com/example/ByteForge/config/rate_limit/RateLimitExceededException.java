package com.example.ByteForge.config.rate_limit;

public class RateLimitExceededException extends Exception{
    public RateLimitExceededException(String s) {
        super(s);
    }
}
