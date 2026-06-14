package com.example.ByteForge.contest.service;

import com.example.ByteForge.contest.dto.request.AddProblemRequestDto;
import com.example.ByteForge.contest.dto.request.ContestCreateRequestDto;
import com.example.ByteForge.contest.dto.response.ContestLeaderboardResponseDto;
import com.example.ByteForge.contest.dto.response.ContestResponseDto;
import com.example.ByteForge.contest.entity.ContestEntity;
import com.example.ByteForge.contest.entity.ContestFinalLeaderboardEntity;
import com.example.ByteForge.contest.entity.ContestProblemEntity;
import com.example.ByteForge.contest.entity.ContestRegistrationEntity;
import com.example.ByteForge.contest.repository.ContestFinalLeaderboardRepository;
import com.example.ByteForge.contest.repository.ContestProblemRepository;
import com.example.ByteForge.contest.repository.ContestRegistrationRepository;
import com.example.ByteForge.contest.repository.ContestRepository;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.services.ProblemService;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final ContestFinalLeaderboardRepository finalLeaderboardRepository;
    private final RedisLeaderboardService redisLeaderboardService;
    private final ProblemService problemService;
    private final UserService userService;

    @Transactional
    public ContestResponseDto createContest(ContestCreateRequestDto request) {
        ContestEntity entity = new ContestEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        ContestEntity saved = contestRepository.save(entity);
        return mapToResponse(saved);
    }

    @Transactional
    public void addProblemToContest(Long contestId, AddProblemRequestDto request) {
        ContestEntity contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        ProblemEntity problem = problemService.findProblemByIdGetEntity(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        ContestProblemEntity cp = new ContestProblemEntity();
        cp.setContest(contest);
        cp.setProblem(problem);
        cp.setScore(request.getScore());
        contestProblemRepository.save(cp);
    }

    public List<ContestResponseDto> getAllContests() {
        return contestRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void registerUser(Long contestId) {
        UserEntity user = userService.getCurrentUserDetailsInEntity();
        ContestEntity contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        if (registrationRepository.existsByContestIdAndUserId(contestId, user.getId())) {
            throw new RuntimeException("Already registered");
        }

        ContestRegistrationEntity reg = new ContestRegistrationEntity();
        reg.setContest(contest);
        reg.setUser(user);
        reg.setRegisteredAt(LocalDateTime.now());
        registrationRepository.save(reg);
    }

    public Object getLeaderboard(Long contestId, int page, int size) {
        contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("Contest not found"));

        if (finalLeaderboardRepository.existsByContestId(contestId)) {
            return finalLeaderboardRepository.findByContestIdOrderByFinalRankAsc(contestId, PageRequest.of(page, size));
        }

        int offset = page * size;
        return redisLeaderboardService.getLeaderboard(contestId, offset, size);
    }

    @Transactional
    public void finalizeContestLeaderboard(Long contestId) {
        if (finalLeaderboardRepository.existsByContestId(contestId)) {
            throw new RuntimeException("Leaderboard already finalized");
        }

        int limit = 10000;
        List<ContestLeaderboardResponseDto> activeLeaderboard = redisLeaderboardService.getLeaderboard(contestId, 0, limit);

        int rank = 1;
        for (ContestLeaderboardResponseDto dto : activeLeaderboard) {
            ContestFinalLeaderboardEntity finalEntity = new ContestFinalLeaderboardEntity();
            finalEntity.setContestId(contestId);
            finalEntity.setUserId(dto.getUserId());
            finalEntity.setUsername(dto.getUsername());
            finalEntity.setScore(dto.getScore());
            finalEntity.setPenaltyTimeInSeconds(dto.getPenaltyTimeInSeconds());
            finalEntity.setFinalRank(rank++);
            finalLeaderboardRepository.save(finalEntity);
        }

        redisLeaderboardService.cleanupContestData(contestId);
    }

    private ContestResponseDto mapToResponse(ContestEntity entity) {
        ContestResponseDto dto = new ContestResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        return dto;
    }
}