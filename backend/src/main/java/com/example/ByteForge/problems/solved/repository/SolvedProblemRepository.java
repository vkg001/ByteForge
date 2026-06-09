package com.example.ByteForge.problems.solved.repository;

import com.example.ByteForge.problems.solved.entity.SolvedProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblemEntity, Long> {
    Optional<SolvedProblemEntity> findByProblemEntity_IdAndUserEntity_Id(Long problemId, Long userId);
}
