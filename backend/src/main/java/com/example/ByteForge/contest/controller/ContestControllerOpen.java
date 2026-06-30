package com.example.ByteForge.contest.controller;

import com.example.ByteForge.contest.dto.response.ContestResponseDto;
import com.example.ByteForge.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
public class ContestControllerOpen {

    private final ContestService contestService;

    @GetMapping("/{pageNumber}")
    public ResponseEntity<List<ContestResponseDto>> getAllContests(@PathVariable("pageNumber") int pageNumber) {
        return ResponseEntity.ok(contestService.getAllContests(pageNumber));
    }
}