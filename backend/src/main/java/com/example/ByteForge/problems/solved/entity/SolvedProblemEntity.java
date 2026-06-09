package com.example.ByteForge.problems.solved.entity;

import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "solved_problem",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_solved_problem_problem_id_and_register_id",
                        columnNames = {"problem_id", "register_id"}
                )
        }
)
public class SolvedProblemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", referencedColumnName = "id", nullable = false)
    private ProblemEntity problemEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;
}
