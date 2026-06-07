package com.example.ByteForge.problems;

import com.example.ByteForge.problems.dto.response.ProblemResponseDto;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.mapper.ProblemMapper;
import com.example.ByteForge.problems.mapper.TestCaseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.ByteForge.config.Constants.PROBLEMS_PER_PAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProblemsService {

    // Constructor injection via Lombok @RequiredArgsConstructor
    private final ProblemsRepository problemsRepository;
    private final ProblemMapper problemMapper;
    private final TestCaseMapper testCaseMapper;

    @Transactional
    public List<ProblemResponseDto> searchProblemByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        List<ProblemEntity> entities = problemsRepository.searchProblemsByKeyword(keyword, pageable);

        return entities.stream()
                .map(entity -> {
                    ProblemResponseDto dto = problemMapper.toResponseDto(entity);
                    // Override default mapping to hide private test cases from public search
                    dto.setTestCases(testCaseMapper.toVisibleResponseDtoList(entity.getTestCases()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<ProblemResponseDto> findProblemById(Long id) {
        return problemsRepository.findById(id).map(entity -> {
            ProblemResponseDto dto = problemMapper.toResponseDto(entity);
            // I added the visibility filter here as well to prevent leaking answers.
            // If this is an admin-only endpoint, remove the line below.
            dto.setTestCases(testCaseMapper.toVisibleResponseDtoList(entity.getTestCases()));
            return dto;
        });
    }

    public Optional<ProblemEntity> findProblemByIdGetEntity(Long id) {
        return problemsRepository.findById(id);
    }

    @Transactional
    public void saveProblem(ProblemEntity problem) {
        problemsRepository.save(problem);
    }
}