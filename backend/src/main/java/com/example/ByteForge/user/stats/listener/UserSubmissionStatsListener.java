package com.example.ByteForge.user.stats.listener;

import com.example.ByteForge.submissions.events.SubmissionUserEvent;
import com.example.ByteForge.user.stats.entity.CalendarActivityEntity;
import com.example.ByteForge.user.stats.entity.UserStats;
import com.example.ByteForge.user.stats.repository.CalendarActivityRepository;
import com.example.ByteForge.user.stats.repository.UserStatsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSubmissionStatsListener {
    private final UserStatsRepository userStatsRepository;
    private final CalendarActivityRepository calendarActivityRepository;

    @Transactional
    @EventListener
    public void recordUserSubmission(SubmissionUserEvent event) {
        // 1. Update overall User Stats
        UserStats stats = userStatsRepository.findByUserEntity_Id(event.userId())
                .orElseThrow(() -> new IllegalStateException("Critical data integrity failure: UserStats missing for user ID " + event.userId()));

        stats.setTotalSubmissions(stats.getTotalSubmissions() + 1);
        stats.setLastSubmissionDate(LocalDateTime.now());
        userStatsRepository.save(stats);

        // 2. Update Daily Calendar Activity
        LocalDate today = LocalDate.now();

        CalendarActivityEntity dailyActivity = calendarActivityRepository
                .findByUserIdAndActivityDate(event.userId(), today)
                .orElseGet(() -> CalendarActivityEntity.builder()
                        .userId(event.userId())
                        .activityDate(today)
                        .submissionCount(0)
                        .build());

        dailyActivity.setSubmissionCount(dailyActivity.getSubmissionCount() + 1);
        calendarActivityRepository.save(dailyActivity);
    }
}