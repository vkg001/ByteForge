package com.example.ByteForge.problems.core.mappers;

import com.example.ByteForge.problems.core.dto.request.TestCaseRequestDto;
import com.example.ByteForge.problems.core.dto.response.TestCaseResponseDto;
import com.example.ByteForge.problems.core.entities.TestCaseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestCaseMapper {

    public TestCaseEntity toEntity(TestCaseRequestDto dto) {
        if (dto == null) {
            return null;
        }

        TestCaseEntity entity = new TestCaseEntity();
        entity.setInput(dto.getInput());
        entity.setOutput(dto.getOutput());

        // Handle potential nulls from the request payload to avoid DB constraint violations
        entity.setHidden(dto.getHidden() != null ? dto.getHidden() : false);
        entity.setHiddenAfterFailure(dto.getHiddenAfterFailure() != null ? dto.getHiddenAfterFailure() : false);

        return entity;
    }

    public List<TestCaseEntity> toEntityList(List<TestCaseRequestDto> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        List<TestCaseEntity> res = new ArrayList<>();
        for (var dto : dtoList) {
            res.add(toEntity(dto));
        }

        return res;
    }

    public TestCaseResponseDto toResponseDto(TestCaseEntity entity) {
        if (entity == null) {
            return null;
        }

        TestCaseResponseDto dto = new TestCaseResponseDto();
        dto.setInput(entity.getInput());
        dto.setOutput(entity.getOutput());
        dto.setHidden(entity.getHidden());
        dto.setHiddenAfterFailure(entity.getHiddenAfterFailure());

        return dto;
    }

    public List<TestCaseResponseDto> toResponseDtoList(List<TestCaseEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<TestCaseResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }

    /**
     * Maps only the test cases that are explicitly NOT hidden.
     */
    public List<TestCaseResponseDto> toVisibleResponseDtoList(List<TestCaseEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<TestCaseResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            // Use Boolean.TRUE.equals to safely evaluate potential nulls
            if (Boolean.TRUE.equals(entity.getHidden())) {
                continue;
            }
            res.add(toResponseDto(entity));
        }

        return res;
    }
}