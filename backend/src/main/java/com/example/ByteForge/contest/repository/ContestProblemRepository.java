package com.example.ByteForge.contest.repository;

import com.example.ByteForge.contest.entity.ContestProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestProblemRepository extends JpaRepository<ContestProblemEntity, Long> {
    List<ContestProblemEntity> findByContestId(Long contestId);
}