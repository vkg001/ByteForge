package com.example.ByteForge.user.stats.controller;

import com.example.ByteForge.user.core.service.UserService;
import com.example.ByteForge.user.stats.dto.response.UserStatsResponseDto;
import com.example.ByteForge.user.stats.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatsResponseDto> getUserStats(@PathVariable Long userId) {
        return userStatsService.getStatsByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserStatsResponseDto> getUserStatsMe() {
        return userStatsService.getStatsByUserId(userService.getCurrentUserDetails().getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}