package com.example.ByteForge.contest.service;

import com.example.ByteForge.contest.dto.response.ContestLeaderboardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisLeaderboardService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long MAX_PENALTY_SECONDS = 999_999_999L;

    public void updateScore(Long contestId, Long userId, String username, int scoreIncrement, long penaltyTimeIncrement) {
        String key = "contest:leaderboard:" + contestId;
        String userHashKey = "contest:user:" + contestId + ":" + userId;

        redisTemplate.opsForHash().put(userHashKey, "username", username);
        redisTemplate.opsForHash().increment(userHashKey, "score", scoreIncrement);
        redisTemplate.opsForHash().increment(userHashKey, "penalty", penaltyTimeIncrement);

        int totalScore = Integer.parseInt((String) redisTemplate.opsForHash().get(userHashKey, "score"));
        long totalPenalty = Long.parseLong((String) redisTemplate.opsForHash().get(userHashKey, "penalty"));

        double zSetScore = (totalScore * 1_000_000_000D) + (MAX_PENALTY_SECONDS - totalPenalty);
        redisTemplate.opsForZSet().add(key, String.valueOf(userId), zSetScore);
    }

    public List<ContestLeaderboardResponseDto> getLeaderboard(Long contestId, int offset, int count) {
        String key = "contest:leaderboard:" + contestId;
        Set<ZSetOperations.TypedTuple<String>> topUsers = redisTemplate.opsForZSet().reverseRangeWithScores(key, offset, offset + count - 1);

        List<ContestLeaderboardResponseDto> leaderboard = new ArrayList<>();
        if (topUsers == null) return leaderboard;

        for (ZSetOperations.TypedTuple<String> tuple : topUsers) {
            Long userId = Long.parseLong(tuple.getValue());
            String userHashKey = "contest:user:" + contestId + ":" + userId;

            String username = (String) redisTemplate.opsForHash().get(userHashKey, "username");
            Integer score = Integer.parseInt((String) redisTemplate.opsForHash().get(userHashKey, "score"));
            Long penalty = Long.parseLong((String) redisTemplate.opsForHash().get(userHashKey, "penalty"));

            ContestLeaderboardResponseDto dto = new ContestLeaderboardResponseDto();
            dto.setUserId(userId);
            dto.setUsername(username);
            dto.setScore(score);
            dto.setPenaltyTimeInSeconds(penalty);
            leaderboard.add(dto);
        }
        return leaderboard;
    }

    public void cleanupContestData(Long contestId) {
        Set<String> keys = redisTemplate.keys("contest:*:" + contestId + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}