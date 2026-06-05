package com.example.ByteForge.user.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String ex) {
        super(ex);
    }
}
