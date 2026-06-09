package com.example.ByteForge.user.core.controller;

import com.example.ByteForge.user.core.dto.response.UserResponseDto;
import com.example.ByteForge.user.core.exception.UserNotFoundException;
import com.example.ByteForge.user.core.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UsersController {
    @Autowired
    private UsersService service;

    @GetMapping("/{Id}")
    public UserResponseDto getUserDetails(@PathVariable("Id") Long Id) {
        var user = service.getUserDetailsById(Id);
        if (user.isEmpty()) throw new UserNotFoundException("Invalid user Id");

        return user.get();
    }

    @GetMapping("/me")
    public UserResponseDto getMyDetails() {
        return service.getCurrentUserDetails();
    }
}
