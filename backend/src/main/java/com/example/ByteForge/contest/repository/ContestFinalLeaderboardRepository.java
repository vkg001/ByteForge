package com.example.ByteForge.contest.repository;

import com.example.ByteForge.contest.entity.ContestFinalLeaderboardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestFinalLeaderboardRepository extends JpaRepository<ContestFinalLeaderboardEntity, Long> {
    Page<ContestFinalLeaderboardEntity> findByContestIdOrderByFinalRankAsc(Long contestId, Pageable pageable);
    boolean existsByContestId(Long contestId);
}