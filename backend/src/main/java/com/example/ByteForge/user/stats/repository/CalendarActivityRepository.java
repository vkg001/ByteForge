package com.example.ByteForge.user.stats.repository;

import com.example.ByteForge.user.stats.entity.CalendarActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarActivityRepository extends JpaRepository<CalendarActivityEntity, Long> {
    Optional<CalendarActivityEntity> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);

    List<CalendarActivityEntity> findByUserIdAndActivityDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}