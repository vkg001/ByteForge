package com.example.ByteForge.problems.stats.service;

import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.stats.dto.response.ProblemStatsResponseDto;
import com.example.ByteForge.problems.stats.entity.ProblemStatsEntity;
import com.example.ByteForge.problems.stats.mapper.ProblemStatsMapper;
import com.example.ByteForge.problems.stats.repository.ProblemStatsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemStatsService {

    private final ProblemStatsRepository problemStatsRepository;
    private final ProblemStatsMapper problemStatsMapper;

    public Optional<ProblemStatsResponseDto> getStatsByProblemId(Long problemId) {
        return problemStatsRepository.findByProblemEntity_Id(problemId)
                .map(problemStatsMapper::toResponseDto);
    }

    @Transactional
    public void recordSubmission(Long problemId, boolean isAccepted) {
        var res = problemStatsRepository.findByProblemEntity_Id(problemId);
        if (res.isEmpty()) throw new ProblemNotFoundException("Invalid problem id");
        ProblemStatsEntity stats = res.get();

        stats.setTotalSubmissions(stats.getTotalSubmissions() + 1);
        if (isAccepted) {
            stats.setTotalAccepted(stats.getTotalAccepted() + 1);
        }

        problemStatsRepository.save(stats);
    }

    @Transactional
    public void incrementLikes(Long problemId) {
        var res = problemStatsRepository.findByProblemEntity_Id(problemId);
        if (res.isEmpty()) throw new ProblemNotFoundException("Invalid problem id");
        ProblemStatsEntity stats = res.get();
        stats.setTotalLikes(stats.getTotalLikes() + 1);
        problemStatsRepository.save(stats);
    }


}