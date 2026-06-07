package com.example.ByteForge.problems.stats.entity;

import com.example.ByteForge.problems.core.entities.ProblemEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "problem_stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemStatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    private ProblemEntity problemEntity;

    @Column(nullable = false)
    private Long totalSubmissions;

    @Column(nullable = false)
    private Long totalAccepted;

    @Column(nullable = false)
    private Long totalLikes;

    @Column(nullable = false)
    private Long totalComments;

    @Column(nullable = false)
    private Long totalSolutionsAvailable;

    @Column(nullable = false)
    private Long totalEditorialsAvailable;

    @Column(nullable = false)
    private Long totalStars;
}
