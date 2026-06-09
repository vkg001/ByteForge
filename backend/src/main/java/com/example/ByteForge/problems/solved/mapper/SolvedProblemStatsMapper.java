package com.example.ByteForge.problems.solved.mapper;

import com.example.ByteForge.problems.solved.dto.response.SolvedProblemStatsResponseDto;
import com.example.ByteForge.problems.solved.entity.SolvedProblemStatsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SolvedProblemStatsMapper {

    public SolvedProblemStatsResponseDto toResponseDto(SolvedProblemStatsEntity entity) {
        if (entity == null) {
            return null;
        }

        SolvedProblemStatsResponseDto dto = new SolvedProblemStatsResponseDto();
        dto.setId(entity.getId());

        // Flatten the relationship safely
        dto.setUserStatsId(entity.getUserStats() != null ? entity.getUserStats().getId() : null);

        dto.setSchool(entity.getSchool());
        dto.setEasy(entity.getEasy());
        dto.setMedium(entity.getMedium());
        dto.setHard(entity.getHard());
        dto.setExtreme(entity.getExtreme());

        return dto;
    }

    public List<SolvedProblemStatsResponseDto> toResponseDtoList(List<SolvedProblemStatsEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<SolvedProblemStatsResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }
}