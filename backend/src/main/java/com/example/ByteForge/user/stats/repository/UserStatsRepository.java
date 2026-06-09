package com.example.ByteForge.user.stats.repository;

import com.example.ByteForge.user.stats.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    Optional<UserStats> findByUserEntity_Id(Long userId);
}