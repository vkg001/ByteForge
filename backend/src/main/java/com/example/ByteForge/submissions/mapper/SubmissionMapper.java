package com.example.ByteForge.submissions.mapper;

import com.example.ByteForge.problems.core.mappers.TestCaseMapper;
import com.example.ByteForge.submissions.dto.response.SubmissionResponseDto;
import com.example.ByteForge.submissions.entity.SubmissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubmissionMapper {

    private final TestCaseMapper testCaseMapper;

    public SubmissionResponseDto toResponseDto(SubmissionEntity entity) {
        if (entity == null) {
            return null;
        }

        SubmissionResponseDto dto = new SubmissionResponseDto();
        dto.setId(entity.getId());
        dto.setLanguageId(entity.getLanguageId());
        dto.setSubmissionCode(entity.getSubmissionCode());

        // Utilize the injected mapper for the nested Test Case DTO
        dto.setFailedOnTestCase(testCaseMapper.toResponseDto(entity.getFailedOnTestCase()));

        dto.setSubmissionStatus(entity.getSubmissionStatus());
        dto.setCodeOutput(entity.getCodeOutput());
        dto.setUserLogs(entity.getUserLogs());
        dto.setSubmissionDateTime(entity.getSubmissionDateTime());

        return dto;
    }

    public List<SubmissionResponseDto> toResponseDtoList(List<SubmissionEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<SubmissionResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }
}