package com.example.ByteForge.auth.signup.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException() {
        super("User already exists with this E-Mail Id");
    }
}
