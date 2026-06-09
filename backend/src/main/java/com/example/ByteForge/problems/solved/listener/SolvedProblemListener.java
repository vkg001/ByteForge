package com.example.ByteForge.problems.solved.listener;

import com.example.ByteForge.problems.solved.entity.SolvedProblemEntity;
import com.example.ByteForge.problems.solved.event.SolvedProblemEvent;
import com.example.ByteForge.problems.solved.event.SolvedProblemStatsEvent;
import com.example.ByteForge.problems.solved.repository.SolvedProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolvedProblemListener {
    private final SolvedProblemRepository repository;
    private final ApplicationEventPublisher publisher;

    @EventListener
    public void handleSolvedProblemEvent(SolvedProblemEvent event) {
        var res = repository.findByProblemEntity_IdAndUserEntity_Id(event.problemEntity().getId(), event.userEntity().getId());
        if (res.isEmpty()) {
            SolvedProblemEntity entity = new SolvedProblemEntity();
            entity.setProblemEntity(event.problemEntity());
            entity.setUserEntity(event.userEntity());
            repository.save(entity);

            SolvedProblemStatsEvent solvedProblemStatsEvent = new SolvedProblemStatsEvent(event.userEntity(), event.problemEntity().getProblemDifficulty());
            publisher.publishEvent(solvedProblemStatsEvent);
        }
    }
}
