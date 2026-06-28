package com.example.ByteForge.submissions.controller;

import com.example.ByteForge.submissions.dto.request.RunCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.RecentSubmissionDto;
import com.example.ByteForge.submissions.dto.response.RunCodeResponseDto;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import com.example.ByteForge.submissions.service.SubmissionService;
import com.example.ByteForge.submissions.dto.request.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.dto.response.SubmissionListResponseDto;
import com.example.ByteForge.user.core.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.ByteForge.config.Constants.PROBLEMS_PER_PAGE;

@RestController
@RequestMapping("/user/submissions")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @GetMapping("/{problem_id}/submissions/{page_number}")
    SubmissionListResponseDto findSubmissionsByProblemAndUserId(@PathVariable("problem_id") Long problemId, @PathVariable("page_number") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        return submissionService.findSubmissionByProblemAndUserId(problemId, userService.getCurrentUserDetailsInEntity().getId(), pageable);
    }

    @GetMapping("/recent")
    public List<RecentSubmissionDto> findRecentSubmissions(
            @RequestParam(value = "limit", defaultValue = "15") int limit) {
        return submissionService.getRecentSubmissions(limit, SubmissionStatus.ACC);
    }

    @PostMapping("/submit")
    public SubmitCodeResponseDto submitCode(@RequestBody SubmitCodeRequestDto requestDto) {
        return submissionService.processSubmission(requestDto);
    }

    @PostMapping("/run")
    public RunCodeResponseDto runCustomCode(@Valid @RequestBody RunCodeRequestDto requestDto) {
        return submissionService.runCustomCode(requestDto);
    }
}
