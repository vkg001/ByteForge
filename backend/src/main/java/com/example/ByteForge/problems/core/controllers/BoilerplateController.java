package com.example.ByteForge.problems.core.controllers;

import com.example.ByteForge.problems.core.dto.request.BoilerplateCodeUpdateRequestDto;
import com.example.ByteForge.problems.core.dto.response.BoilerplateCodeUpdateResponseDto;
import com.example.ByteForge.problems.core.services.BoilerplateCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/boilerplate")
@Slf4j
@RequiredArgsConstructor
public class BoilerplateController {

    private final BoilerplateCodeService boilerplateCodeService;

    @PostMapping("/update")
    ResponseEntity<BoilerplateCodeUpdateResponseDto> updateBoilerplateCode(@RequestBody BoilerplateCodeUpdateRequestDto request) {
        var response = boilerplateCodeService.updateBoilerplateCode(request);
        if (response.getStatus() == HttpStatus.ACCEPTED) return ResponseEntity.ok(response);

        return new ResponseEntity<>(response, response.getStatus());
    }
}
