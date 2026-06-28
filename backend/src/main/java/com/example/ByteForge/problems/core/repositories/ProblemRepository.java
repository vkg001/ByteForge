package com.example.ByteForge.problems.core.repositories;

import com.example.ByteForge.problems.core.entities.ProblemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query(
            value = "SELECT * FROM problems " +
                    "WHERE problem_title ILIKE CONCAT('%', :keyword, '%') " +
                    "AND problem_visibility = 'PUBLIC' " +
                    "ORDER BY problem_title <-> CAST(:keyword AS text) ASC",
            nativeQuery = true
    )
    List<ProblemEntity> searchProblemsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    Page<ProblemEntity> findByProblemVisibility(String problemVisibility, Pageable pageable);
}