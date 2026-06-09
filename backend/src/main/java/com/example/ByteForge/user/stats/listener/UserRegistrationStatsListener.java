package com.example.ByteForge.user.stats.listener;

import com.example.ByteForge.problems.solved.entity.SolvedProblemStatsEntity;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.stats.events.UserRegisteredEvent;
import com.example.ByteForge.user.stats.entity.UserStats;
import com.example.ByteForge.user.stats.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationStatsListener {

    private final UserStatsRepository userStatsRepository;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        if (userStatsRepository.findByUserEntity_Id(event.userId()).isEmpty()) {
            log.info("Initializing UserStats for new user ID: {}", event.userId());

            UserEntity userReference = new UserEntity();
            userReference.setId(event.userId());

            UserStats stats = new UserStats();
            stats.setUserEntity(userReference);
            stats.setTotalSubmissions(0L);
            stats.setReputation(0L);
            stats.setTotalComments(0L);
            stats.setTotalSolutionsAdded(0L);

            SolvedProblemStatsEntity problemStats = new SolvedProblemStatsEntity();
            problemStats.setUserStats(stats);

            stats.setProblemStats(problemStats);

            userStatsRepository.save(stats);
        }
    }
}