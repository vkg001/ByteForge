package com.example.ByteForge.user.stats.controller;

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

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsResponseDto> getUserStats(@PathVariable Long userId) {
        return userStatsService.getStatsByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}