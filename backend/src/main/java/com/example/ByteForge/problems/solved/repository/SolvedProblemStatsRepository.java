package com.example.ByteForge.problems.solved.repository;

import com.example.ByteForge.problems.solved.entity.SolvedProblemStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolvedProblemStatsRepository extends JpaRepository<SolvedProblemStatsEntity, Long> {
    SolvedProblemStatsEntity findSolvedProblemStatsByUserStats_Id(Long Id);
}
