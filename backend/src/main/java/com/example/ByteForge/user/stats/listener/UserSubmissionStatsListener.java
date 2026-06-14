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
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSubmissionStatsListener {
    private final UserStatsRepository userStatsRepository;
    private final CalendarActivityRepository calendarActivityRepository;

    @Transactional
    @EventListener
    public void recordUserSubmission(SubmissionUserEvent event) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        UserStats stats = userStatsRepository.findByUserEntity_Id(event.userId())
                .orElseThrow(() -> new IllegalStateException("Critical data integrity failure: UserStats missing for user ID " + event.userId()));

        LocalDate lastSubmissionDay = stats.getLastSubmissionDate() != null
                ? stats.getLastSubmissionDate().atZone(ZoneOffset.UTC).toLocalDate()
                : null;

        if (lastSubmissionDay == null || lastSubmissionDay.isBefore(today.minusDays(1))) {
            stats.setCurrentStreak(1);
        } else if (lastSubmissionDay.isEqual(today.minusDays(1))) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        }

        if (stats.getCurrentStreak() > stats.getMaxStreak()) {
            stats.setMaxStreak(stats.getCurrentStreak());
        }

        stats.setTotalSubmissions(stats.getTotalSubmissions() + 1);
        stats.setLastSubmissionDate(LocalDateTime.now(ZoneOffset.UTC));
        userStatsRepository.save(stats);

        // 2. Update Daily Calendar Activity
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