package com.example.ByteForge.problems.core.mappers;

import com.example.ByteForge.problems.core.dto.request.ProblemRequestDto;
import com.example.ByteForge.problems.core.dto.response.ProblemResponseDto;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ProblemMapper {

    private final ExampleMapper exampleMapper;
    private final BoilerplateCodeMapper boilerPlateCodeMapper;
    private final TestCaseMapper testCaseMapper; // Assuming you have this implemented similarly

    public ProblemEntity toEntity(ProblemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ProblemEntity entity = new ProblemEntity();
        entity.setProblemTitle(dto.getProblemTitle());
        entity.setProblemStatement(dto.getProblemStatement());
        entity.setProblemVisibility(dto.getProblemVisibility());

        // Deep copy lists to avoid reference mutations
        entity.setConstraints(dto.getConstraints() != null ? new ArrayList<>(dto.getConstraints()) : new ArrayList<>());
        entity.setHints(dto.getHints() != null ? new ArrayList<>(dto.getHints()) : new ArrayList<>());
        entity.setCompanyTags(dto.getCompanyTags() != null ? new ArrayList<>(dto.getCompanyTags()) : new ArrayList<>());
        entity.setTopics(dto.getTopics() != null ? new ArrayList<>(dto.getTopics()) : new ArrayList<>());
        entity.setSimilarQuestions(dto.getSimilarQuestions() != null ? new ArrayList<>(dto.getSimilarQuestions()) : new ArrayList<>());

        entity.setProblemDifficulty(dto.getProblemDifficulty());
        entity.setMemoryLimitInMB(dto.getMemoryLimitInMB());
        entity.setTimeLimitInMS(dto.getTimeLimitInMS());

        // Map complex nested objects using injected mappers
        entity.setExamples(exampleMapper.toEntityList(dto.getExamples()));
        entity.setBoilerPlateCodes(boilerPlateCodeMapper.toEntityList(dto.getBoilerPlateCodes()));

        // This utilizes your custom setTestCases method in the entity to maintain the bidirectional relationship
        entity.setTestCases(testCaseMapper.toEntityList(dto.getTestCases()));

        return entity;
    }

    public ProblemResponseDto toResponseDto(ProblemEntity entity) {
        if (entity == null) {
            return null;
        }

        ProblemResponseDto dto = new ProblemResponseDto();
        dto.setProblemTitle(entity.getProblemTitle());
        dto.setProblemStatement(entity.getProblemStatement());

        dto.setConstraints(entity.getConstraints() != null ? new ArrayList<>(entity.getConstraints()) : new ArrayList<>());
        dto.setHints(entity.getHints() != null ? new ArrayList<>(entity.getHints()) : new ArrayList<>());
        dto.setCompanyTags(entity.getCompanyTags() != null ? new ArrayList<>(entity.getCompanyTags()) : new ArrayList<>());
        dto.setTopics(entity.getTopics() != null ? new ArrayList<>(entity.getTopics()) : new ArrayList<>());
        dto.setSimilarQuestions(entity.getSimilarQuestions() != null ? new ArrayList<>(entity.getSimilarQuestions()) : new ArrayList<>());

        dto.setProblemDifficulty(entity.getProblemDifficulty());
        dto.setMemoryLimitInMB(entity.getMemoryLimitInMB());
        dto.setTimeLimitInMS(entity.getTimeLimitInMS());

        // Map complex nested objects back to DTOs
        dto.setExamples(exampleMapper.toResponseDtoList(entity.getExamples()));

        // Note: BoilerplateCodeMapper requires the boolean flags based on your previous design.
        // I am defaulting to false here to match your original ResponseDto logic. Adjust if necessary.
        dto.setBoilerPlateCodes(boilerPlateCodeMapper.toResponseDtoList(entity.getBoilerPlateCodes(), false, false));

        dto.setTestCases(testCaseMapper.toResponseDtoList(entity.getTestCases()));

        return dto;
    }
}