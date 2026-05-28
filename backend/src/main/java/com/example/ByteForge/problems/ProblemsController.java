package com.example.ByteForge.problems;

import java.util.List;

import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@Slf4j
public class ProblemsController {
    @Autowired
    private ProblemsService problemsService;

    @GetMapping("/")
    public ResponseEntity<ProblemEntity> getProblemById(@RequestParam Long id) {
        var res = problemsService.findProblemById(id);
        if (res.isPresent()) {
            return ResponseEntity.ok(res.get());
        }

        throw new ProblemNotFoundException("Problem does not exist with the provided id.");
    }

    @GetMapping("/search-problem")
    public ResponseEntity<List<ProblemEntity>> searchProblemByKeyword(@RequestParam String keyword, @RequestParam Integer pageNumber) {
        return ResponseEntity.ok(problemsService.searchProblemByKeyword(keyword, pageNumber));
    }
}
