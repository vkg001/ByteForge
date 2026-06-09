package com.example.ByteForge.user.stats.service;

import com.example.ByteForge.user.stats.dto.response.UserStatsResponseDto;
import com.example.ByteForge.user.stats.mapper.UserStatsMapper;
import com.example.ByteForge.user.stats.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsService {

    private final UserStatsRepository userStatsRepository;
    private final UserStatsMapper userStatsMapper;

    public Optional<UserStatsResponseDto> getStatsByUserId(Long userId) {
        return userStatsRepository.findByUserEntity_Id(userId)
                .map(userStatsMapper::toResponseDto);
    }
}