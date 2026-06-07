package com.example.ByteForge.problems.core.controllers;

import java.util.List;

import com.example.ByteForge.problems.core.dto.response.ProblemResponseDto;
import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.core.services.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems/core")
@Slf4j
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @GetMapping("/{id}/description")
    public ResponseEntity<ProblemResponseDto> getProblemById(@PathVariable Long id) {
        var res = problemService.findProblemById(id);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        }

        throw new ProblemNotFoundException("Problem does not exist with the provided id.");
    }

    @GetMapping("/search-problem/{pageNumber}")
    public ResponseEntity<List<ProblemResponseDto>> searchProblemByKeyword(@RequestParam String keyword, @PathVariable Integer pageNumber) {
        return ResponseEntity.ok(problemService.searchProblemByKeyword(keyword, pageNumber));
    }
}
