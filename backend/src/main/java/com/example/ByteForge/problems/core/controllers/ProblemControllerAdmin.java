package com.example.ByteForge.problems.core.controllers;

import com.example.ByteForge.problems.core.mappers.ProblemMapper;
import com.example.ByteForge.problems.core.services.ProblemService;
import com.example.ByteForge.utility.SimpleMessageDto;
import com.example.ByteForge.problems.core.dto.request.ProblemRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/problems/core")
public class ProblemControllerAdmin {
    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemMapper problemMapper;

    @PostMapping("/add-problem")
    public ResponseEntity<SimpleMessageDto> addProblem(@RequestBody ProblemRequestDto requestDto) {
        problemService.saveProblem(problemMapper.toEntity(requestDto));
        var res = new SimpleMessageDto("Problem saved", HttpStatus.ACCEPTED);
        return ResponseEntity.ok(res);
    }
}
