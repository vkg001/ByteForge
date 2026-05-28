package com.example.ByteForge.config;

import com.example.ByteForge.auth.signup.exceptions.InvalidOtpException;
import com.example.ByteForge.auth.signup.exceptions.UserAlreadyExistsException;
import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<SimpleMessageDto> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        SimpleMessageDto errorResponse = new SimpleMessageDto(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<SimpleMessageDto> handleInvalidOtp(InvalidOtpException ex) {
        SimpleMessageDto errorResponse = new SimpleMessageDto(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<SimpleMessageDto> handleUserNameNotFound(UsernameNotFoundException ex) {
        SimpleMessageDto errorResponse = new SimpleMessageDto(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProblemNotFoundException.class)
    public ResponseEntity<SimpleMessageDto> handleProblemNotFoundException(ProblemNotFoundException ex) {
        SimpleMessageDto errorResponse = new SimpleMessageDto(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleMessageDto> handleGeneralException(Exception ex) {
        SimpleMessageDto errorResponse = new SimpleMessageDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
