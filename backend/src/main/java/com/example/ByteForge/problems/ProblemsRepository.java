package com.example.ByteForge.problems;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface ProblemsRepository extends JpaRepository<ProblemEntity, Long> {

    @Query(value = "Select * FROM problems WHERE problem_title % :keyword ORDER BY similarity (problem_title, :keyword) DESC", nativeQuery = true)
    List<ProblemEntity> searchProblemsByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
