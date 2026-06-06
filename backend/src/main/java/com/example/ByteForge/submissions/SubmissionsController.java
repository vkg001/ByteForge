package com.example.ByteForge.submissions;

import com.example.ByteForge.submissions.dto.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.dto.SubmissionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/submissions")
public class SubmissionsController {
    @Autowired
    private SubmissionsService submissionsService;

    @GetMapping("/{problem_id}/submissions")
    List<SubmissionDto> findSubmissionsByProblemAndUserId(@PathVariable("problem_id") Long id) {
        throw new RuntimeException("Unimplemented");
    }

    @PostMapping("/submit")
    public SubmitCodeResponseDto submitCode(@RequestBody SubmitCodeRequestDto requestDto) {
        return submissionsService.processSubmission(requestDto);
    }
}
