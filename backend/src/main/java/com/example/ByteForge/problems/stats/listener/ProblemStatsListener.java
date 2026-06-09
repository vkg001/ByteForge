package com.example.ByteForge.problems.stats.listener;

import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.stats.entity.ProblemStatsEntity;
import com.example.ByteForge.problems.stats.events.ProblemSavedEvent;
import com.example.ByteForge.problems.stats.repository.ProblemStatsRepository;
import com.example.ByteForge.submissions.events.SubmissionEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProblemStatsListener {

    private final ProblemStatsRepository problemStatsRepository;

    @Transactional
    @EventListener
    public void recordSubmission(SubmissionEvent event) {
        var res = problemStatsRepository.findByProblemEntity_Id(event.problemId());

        if (res.isEmpty()) {
            throw new ProblemNotFoundException("Invalid problem id: " + event.problemId());
        }

        ProblemStatsEntity stats = res.get();
        stats.setTotalSubmissions(stats.getTotalSubmissions() + 1);

        if (event.isAccepted()) {
            stats.setTotalAccepted(stats.getTotalAccepted() + 1);
        }

        problemStatsRepository.save(stats);
    }

    @Transactional
    @EventListener
    public void initializeStats(ProblemSavedEvent event) {
        ProblemStatsEntity stats = new ProblemStatsEntity();

        ProblemEntity problemEntity = new ProblemEntity();
        problemEntity.setId(event.problemId());
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