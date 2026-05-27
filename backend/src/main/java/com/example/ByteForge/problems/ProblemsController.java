package com.example.ByteForge.problems;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/problems")
@Slf4j
public class ProblemsController {
    @Autowired
    private ProblemsService problemsService;

    @PostMapping("/search-problem")
    public ResponseEntity<List<ProblemEntity>> searchProblemByKeyword(@RequestParam String keyword, @RequestParam Integer pageNumber) {
        var res = problemsService.searchProblemByKeyword(keyword, pageNumber);
        if (res == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(res);
    }


}
