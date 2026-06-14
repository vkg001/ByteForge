package com.example.ByteForge.problems.solved.listener;

import com.example.ByteForge.problems.solved.entity.SolvedProblemEntity;
import com.example.ByteForge.problems.solved.event.SolvedProblemEvent;
import com.example.ByteForge.problems.solved.event.SolvedProblemStatsEvent;
import com.example.ByteForge.problems.solved.repository.SolvedProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolvedProblemListener {

    private static final Logger log = LoggerFactory.getLogger(SolvedProblemListener.class);
    private final SolvedProblemRepository repository;
    private final ApplicationEventPublisher publisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void handleSolvedProblemEvent(SolvedProblemEvent event) {
        var res = repository.findByProblemEntityIdAndUserEntityId(event.problemEntity().getId(), event.userEntity().getId());

        if (res.isEmpty()) {
            SolvedProblemEntity entity = new SolvedProblemEntity();
            entity.setProblemEntity(event.problemEntity());
            entity.setUserEntity(event.userEntity());

            try {
                repository.saveAndFlush(entity);
                SolvedProblemStatsEvent solvedProblemStatsEvent = new SolvedProblemStatsEvent(
                        event.userEntity().getId(),
                        event.problemEntity().getProblemDifficulty()
                );
                publisher.publishEvent(solvedProblemStatsEvent);
            } catch (DataIntegrityViolationException ignored) {
            }
        }
    }
}