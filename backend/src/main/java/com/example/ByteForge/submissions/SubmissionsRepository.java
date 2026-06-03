package com.example.ByteForge.submissions;

import com.example.ByteForge.submissions.entities.SubmissionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface SubmissionsRepository extends JpaRepository<SubmissionEntity, Long> {
    @Query(value = "Select * from submissions WHERE problem_id = :problemId AND register_id = :userId ORDER BY id DESC", nativeQuery = true)
    List<SubmissionEntity> findSubmissionByProblemAndUserId(@Param("problemId") Long problemId, @Param("userId") Long userId, Pageable pageable);
}
