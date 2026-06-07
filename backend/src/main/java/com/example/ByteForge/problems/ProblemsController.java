package com.example.ByteForge.problems;

import java.util.List;

import com.example.ByteForge.problems.dto.response.ProblemResponseDto;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@Slf4j
public class ProblemsController {
    @Autowired
    private ProblemsService problemsService;

    @GetMapping("/{id}/description")
    public ResponseEntity<ProblemResponseDto> getProblemById(@PathVariable Long id) {
        var res = problemsService.findProblemById(id);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        }

        throw new ProblemNotFoundException("Problem does not exist with the provided id.");
    }

    @GetMapping("/search-problem/{pageNumber}")
    public ResponseEntity<List<ProblemResponseDto>> searchProblemByKeyword(@RequestParam String keyword, @PathVariable Integer pageNumber) {
        return ResponseEntity.ok(problemsService.searchProblemByKeyword(keyword, pageNumber));
    }
}
