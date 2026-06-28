package com.example.ByteForge.submissions.mapper;

import com.example.ByteForge.problems.core.mappers.TestCaseMapper;
import com.example.ByteForge.submissions.dto.response.SubmissionResponseDto;
import com.example.ByteForge.submissions.entity.SubmissionEntity;
import com.example.ByteForge.submissions.enums.SubmissionStatus; // Ensure correct import
import com.example.ByteForge.submissions.utils.CodeOutputParser; // Adjust package based on where you put the parser
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
        dto.setFailedOnTestCase(testCaseMapper.toResponseDto(entity.getFailedOnTestCase()));
        dto.setSubmissionStatus(entity.getSubmissionStatus());
        dto.setSubmissionDateTime(entity.getSubmissionDateTime());

        // Presentation Logic: Hide outputs on Accepted, Parse outputs on Failure
        if (entity.getSubmissionStatus() == SubmissionStatus.ACC) {
            dto.setCodeOutput(null);
            dto.setUserLogs(null);
        } else {
            // Parses the raw "~CASE_BEGIN~..." string into a List<TestCaseExecutionDto>
            dto.setCodeOutput(CodeOutputParser.parseRawOutput(entity.getCodeOutput()));
            dto.setUserLogs(entity.getUserLogs());
        }

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