package com.example.ByteForge.contest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contest_final_leaderboards", indexes = {
        @Index(name = "idx_contest_rank", columnList = "contest_id, final_rank")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContestFinalLeaderboardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long contestId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Long penaltyTimeInSeconds;

    @Column(nullable = false)
    private Integer finalRank;
}