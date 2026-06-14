package com.example.ByteForge.user.core.controller;

import com.example.ByteForge.user.core.dto.response.UserResponseDto;
import com.example.ByteForge.user.core.exception.UserNotFoundException;
import com.example.ByteForge.user.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    public UserResponseDto getUserDetails(@PathVariable("id") Long id) {
        return service.getUserDetailsById(id)
                .orElseThrow(() -> new UserNotFoundException("Invalid user Id"));
    }

    @GetMapping("/me")
    public UserResponseDto getMyDetails() {
        return service.getCurrentUserDetails();
    }
}