package com.example.ByteForge.problems.stats.repository;

import com.example.ByteForge.problems.stats.entity.ProblemStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemStatsRepository extends JpaRepository<ProblemStatsEntity, Long> {
    Optional<ProblemStatsEntity> findByProblemEntity_Id(Long problemId);
}
