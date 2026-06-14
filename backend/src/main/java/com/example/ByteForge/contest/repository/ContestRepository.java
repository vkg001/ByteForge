package com.example.ByteForge.contest.repository;

import com.example.ByteForge.contest.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<ContestEntity, Long> {}