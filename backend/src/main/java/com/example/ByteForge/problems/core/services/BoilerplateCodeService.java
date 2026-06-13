package com.example.ByteForge.problems.core.services;

import com.example.ByteForge.problems.core.dto.request.BoilerplateCodeUpdateRequestDto;
import com.example.ByteForge.problems.core.dto.response.BoilerplateCodeUpdateResponseDto;
import com.example.ByteForge.problems.core.entities.BoilerplateCodeEntity;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.core.repositories.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoilerplateCodeService {
    private final ProblemRepository problemRepository;

    public BoilerplateCodeUpdateResponseDto updateBoilerplateCode(BoilerplateCodeUpdateRequestDto request) {
        ProblemEntity problem = problemRepository.findById(request.getProblemId()).orElseThrow(
                () -> new ProblemNotFoundException("Problem not found !!")
        );

        BoilerplateCodeUpdateResponseDto responseDto = new BoilerplateCodeUpdateResponseDto();
        responseDto.setMessage("Unable to update");
        responseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        boolean updated = false;
        for (var boilers: problem.getBoilerPlateCodes()) {
            if (boilers.getLanguageCode() == request.getLanguageCode()) {
                boilers.setAppendCode(request.getAppendCode());
                boilers.setPrependCode(request.getPrependCode());
                boilers.setUserCode(request.getUserCode());
                responseDto.setStatus(HttpStatus.ACCEPTED);
                responseDto.setMessage("Boilerplate Codes have been updated !!");
                updated = true;
                break;
            }
        }

        if (!updated) {
            BoilerplateCodeEntity entity = new BoilerplateCodeEntity();

            entity.setLanguageCode(request.getLanguageCode());
            entity.setPrependCode(request.getPrependCode());
            entity.setAppendCode(request.getAppendCode());
            entity.setUserCode(request.getUserCode());

            responseDto.setStatus(HttpStatus.ACCEPTED);
            responseDto.setMessage("Boilerplate Codes has been added !!");

            problem.getBoilerPlateCodes().add(entity);
        }

        problemRepository.save(problem);

        return responseDto;
    }
}
