package com.example.ByteForge.user.stats.listener;

import com.example.ByteForge.problems.solved.event.SolvedProblemStatsEvent;
import com.example.ByteForge.user.stats.entity.UserStats;
import com.example.ByteForge.user.stats.repository.UserStatsRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSolvedProblemStatsListener {

    private final UserStatsRepository userStatsRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProblemSolvedEvent(SolvedProblemStatsEvent event) {
        UserStats stats = userStatsRepository.findByUserEntity_Id(event.userId())
                .orElseThrow(() -> new IllegalStateException("Critical data integrity failure: UserStats missing for user ID " + event.userId()));

        stats.setTotalSubmissions(stats.getTotalSubmissions() + 1);
        stats.setLastSubmissionDate(LocalDateTime.now());

        var problemStats = stats.getProblemStats();
        if (problemStats == null) {
            throw new IllegalStateException("ProblemStats relationship is null for UserStats ID " + stats.getId());
        }
        log.warn("Problem Difficulty: {}", event.problemDifficulty());
        switch (event.problemDifficulty()) {
            case SCHOOL -> stats.getProblemStats().setSchool(problemStats.getSchool() + 1);
            case EASY -> stats.getProblemStats().setEasy(problemStats.getEasy() + 1);
            case MEDIUM -> stats.getProblemStats().setMedium(problemStats.getMedium() + 1);
            case HARD -> stats.getProblemStats().setHard(problemStats.getHard() + 1);
            case EXTREME -> stats.getProblemStats().setExtreme(problemStats.getExtreme() + 1);
        }

        userStatsRepository.save(stats);
    }
}