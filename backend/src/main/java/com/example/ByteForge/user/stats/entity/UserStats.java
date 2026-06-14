package com.example.ByteForge.user.stats.entity;

import com.example.ByteForge.problems.solved.entity.SolvedProblemStatsEntity;
import com.example.ByteForge.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private Long totalSubmissions = 0L;

    @OneToOne(mappedBy = "userStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private SolvedProblemStatsEntity problemStats;

    @Column(nullable = true)
    private LocalDateTime lastSubmissionDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastActivityDate;

    @Column(nullable = false)
    private Long reputation = 0L;

    @Column(nullable = false)
    private Long totalComments = 0L;

    @Column(nullable = false)
    private Long totalSolutionsAdded = 0L;

    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Column(nullable = false)
    private Integer maxStreak = 0;
}