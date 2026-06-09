package com.example.ByteForge.submissions.repository;

import com.example.ByteForge.submissions.entity.SubmissionEntity;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    @Query(value = "Select * from submissions WHERE problem_id = :problemId AND register_id = :userId ORDER BY id DESC", nativeQuery = true)
    List<SubmissionEntity> findSubmissionByProblemAndUserId(@Param("problemId") Long problemId, @Param("userId") Long userId, Pageable pageable);

    @Query(value = "Select * from submissions WHERE problem_id = :problemId AND register_id = :userId AND status = :status ORDER BY id DESC", nativeQuery = true)
    Optional<SubmissionEntity> findSubmissionByProblemUserIdAndStatus(@Param("problemId") Long problemId, @Param("userId") Long userId, @Param("status") SubmissionStatus status);
}
