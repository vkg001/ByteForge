package com.example.ByteForge.user;

import com.example.ByteForge.user.exceptions.UserNotFoundException;
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
    public UserDto getUserDetails(@PathVariable("Id") Long Id) {
        var user = service.getUserDetailsById(Id);
        if (user.isEmpty()) throw new UserNotFoundException("Invalid user Id");

        return user.get();
    }

    @GetMapping("/me")
    public UserDto getMyDetails() {
        return service.getCurrentUserDetails();
    }
}
