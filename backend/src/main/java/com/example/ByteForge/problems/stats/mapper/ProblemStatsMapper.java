package com.example.ByteForge.problems.stats.mapper;

import com.example.ByteForge.problems.stats.dto.response.ProblemStatsResponseDto;
import com.example.ByteForge.problems.stats.entity.ProblemStatsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProblemStatsMapper {

    public ProblemStatsResponseDto toResponseDto(ProblemStatsEntity entity) {
        if (entity == null) {
            return null;
        }

        ProblemStatsResponseDto dto = new ProblemStatsResponseDto();
        dto.setId(entity.getId());

        // Extract only the ID from the related ProblemEntity to prevent data bloat
        dto.setProblemId(entity.getProblemEntity() != null ? entity.getProblemEntity().getId() : null);

        dto.setTotalSubmissions(entity.getTotalSubmissions());
        dto.setTotalAccepted(entity.getTotalAccepted());
        dto.setTotalLikes(entity.getTotalLikes());
        dto.setTotalComments(entity.getTotalComments());
        dto.setTotalSolutionsAvailable(entity.getTotalSolutionsAvailable());
        dto.setTotalEditorialsAvailable(entity.getTotalEditorialsAvailable());
        dto.setTotalStars(entity.getTotalStars());

        return dto;
    }

    public List<ProblemStatsResponseDto> toResponseDtoList(List<ProblemStatsEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<ProblemStatsResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }
}