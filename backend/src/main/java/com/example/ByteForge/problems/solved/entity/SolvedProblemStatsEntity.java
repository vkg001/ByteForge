package com.example.ByteForge.problems.solved.entity;

import com.example.ByteForge.user.stats.entity.UserStats;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "solved_problem_stats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_solved_problem_stats_user",
                        columnNames = "user_stats_id" // FIXED: Changed from 'register_id' to the actual column
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedProblemStatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_stats_id", referencedColumnName = "id")
    private UserStats userStats;

    @Column(nullable = false)
    private Long school = 0L;

    @Column(nullable = false)
    private Long easy = 0L;

    @Column(nullable = false)
    private Long medium = 0L;

    @Column(nullable = false)
    private Long hard = 0L;

    @Column(nullable = false)
    private Long extreme = 0L;
}