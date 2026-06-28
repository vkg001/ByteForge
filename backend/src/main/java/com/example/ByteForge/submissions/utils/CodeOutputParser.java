package com.example.ByteForge.submissions.utils;

import com.example.ByteForge.submissions.dto.response.TestCaseExecutionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeOutputParser {

    private static final Pattern CASE_PATTERN = Pattern.compile(
            "~CASE_BEGIN~\\s*~USER_LOGS~\\s*(.*?)\\s*~FUNC_OUT~\\s*(.*?)\\s*~TIME\\|([\\d.]+)~\\s*~CASE_END~",
            Pattern.DOTALL
    );

    public static List<TestCaseExecutionDto> parseRawOutput(String rawOutput) {
        if (rawOutput == null || rawOutput.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<TestCaseExecutionDto> parsedResults = new ArrayList<>();
        Matcher matcher = CASE_PATTERN.matcher(rawOutput);

        while (matcher.find()) {
            TestCaseExecutionDto dto = new TestCaseExecutionDto();

            String userLogs = matcher.group(1).trim();
            dto.setUserLogs(userLogs.isEmpty() ? null : userLogs);

            dto.setFuncOut(matcher.group(2).trim());
            dto.setExecutionTime(matcher.group(3).trim());

            parsedResults.add(dto);
        }

        return parsedResults;
    }
}
