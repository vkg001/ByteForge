package com.example.ByteForge.submissions;

import com.example.ByteForge.submissions.dto.request.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.dto.response.SubmissionsListResponseDto;
import com.example.ByteForge.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import static com.example.ByteForge.config.Constants.PROBLEMS_PER_PAGE;

@RestController
@RequestMapping("/user/submissions")
public class SubmissionsController {
    @Autowired
    private SubmissionsService submissionsService;

    @Autowired
    private UsersService usersService;

    @GetMapping("/{problem_id}/submissions/{page_number}")
    SubmissionsListResponseDto findSubmissionsByProblemAndUserId(@PathVariable("problem_id") Long problemId, @PathVariable("page_number") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        return submissionsService.findSubmissionByProblemAndUserId(problemId, usersService.getCurrentUserDetailsInEntity().getId(), pageable);
    }

    @PostMapping("/submit")
    public SubmitCodeResponseDto submitCode(@RequestBody SubmitCodeRequestDto requestDto) {
        return submissionsService.processSubmission(requestDto);
    }
}
