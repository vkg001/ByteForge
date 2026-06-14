package com.example.ByteForge.user.stats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "calendar_activities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "activity_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "submission_count", nullable = false)
    private int submissionCount;
}