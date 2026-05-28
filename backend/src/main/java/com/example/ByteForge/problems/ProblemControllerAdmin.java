package com.example.ByteForge.problems;

import com.example.ByteForge.common.SimpleMessageDto;
import com.example.ByteForge.problems.entities.ProblemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/problems")
public class ProblemControllerAdmin {
    @Autowired
    private ProblemsService problemsService;

    @PostMapping("/add-problem")
    public ResponseEntity<SimpleMessageDto> addProblem(@RequestBody ProblemEntity problemEntity) {
        problemsService.saveProblem(problemEntity);
        var res = new SimpleMessageDto("Problem saved", HttpStatus.ACCEPTED);
        return ResponseEntity.ok(res);
    }
}
