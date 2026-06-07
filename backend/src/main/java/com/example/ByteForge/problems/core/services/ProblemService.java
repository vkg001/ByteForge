package com.example.ByteForge.problems.core.services;

import com.example.ByteForge.problems.core.dto.response.ProblemResponseDto;
import com.example.ByteForge.problems.core.mappers.ProblemMapper;
import com.example.ByteForge.problems.core.mappers.TestCaseMapper;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.repositories.ProblemRepository;
import com.example.ByteForge.problems.stats.entity.ProblemStatsEntity;
import com.example.ByteForge.problems.stats.repository.ProblemStatsRepository;
import com.example.ByteForge.problems.stats.service.ProblemStatsService;
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
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;
    private final TestCaseMapper testCaseMapper;
    private final ProblemStatsRepository problemStatsRepository;

    @Transactional
    public List<ProblemResponseDto> searchProblemByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        List<ProblemEntity> entities = problemRepository.searchProblemsByKeyword(keyword, pageable);

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
        return problemRepository.findById(id).map(entity -> {
            ProblemResponseDto dto = problemMapper.toResponseDto(entity);
            dto.setTestCases(testCaseMapper.toVisibleResponseDtoList(entity.getTestCases()));
            return dto;
        });
    }

    public Optional<ProblemEntity> findProblemByIdGetEntity(Long id) {
        return problemRepository.findById(id);
    }

    @Transactional
    public void saveProblem(ProblemEntity problem) {
        problemRepository.save(problem);
        initializeStats(problem);
    }

    private void initializeStats(ProblemEntity problemEntity) {
        ProblemStatsEntity stats = new ProblemStatsEntity();

        stats.setProblemEntity(problemEntity);

        stats.setTotalSubmissions(0L);
        stats.setTotalAccepted(0L);
        stats.setTotalLikes(0L);
        stats.setTotalComments(0L);
        stats.setTotalSolutionsAvailable(0L);
        stats.setTotalEditorialsAvailable(0L);
        stats.setTotalStars(0L);

        problemStatsRepository.save(stats);
    }
}