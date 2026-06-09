package com.example.ByteForge.user.stats.mapper;

import com.example.ByteForge.problems.solved.mapper.SolvedProblemStatsMapper;
import com.example.ByteForge.user.stats.dto.response.UserStatsResponseDto;
import com.example.ByteForge.user.stats.entity.UserStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStatsMapper {

    private final SolvedProblemStatsMapper solvedProblemStatsMapper;

    public UserStatsResponseDto toResponseDto(UserStats entity) {
        if (entity == null) {
            return null;
        }

        UserStatsResponseDto dto = new UserStatsResponseDto();
        dto.setId(entity.getId());

        // Flatten UserEntity to ID safely
        dto.setUserId(entity.getUserEntity() != null ? entity.getUserEntity().getId() : null);

        dto.setTotalSubmissions(entity.getTotalSubmissions());

        // Delegate mapping of the nested stats entity to the injected mapper
        dto.setProblemStats(solvedProblemStatsMapper.toResponseDto(entity.getProblemStats()));

        dto.setLastSubmissionDate(entity.getLastSubmissionDate());
        dto.setLastActivityDate(entity.getLastActivityDate());
        dto.setReputation(entity.getReputation());
        dto.setTotalComments(entity.getTotalComments());
        dto.setTotalSolutionsAdded(entity.getTotalSolutionsAdded());

        return dto;
    }

    public List<UserStatsResponseDto> toResponseDtoList(List<UserStats> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<UserStatsResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }
}