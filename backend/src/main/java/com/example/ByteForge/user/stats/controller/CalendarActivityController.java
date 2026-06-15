package com.example.ByteForge.user.stats.controller;

import com.example.ByteForge.user.core.service.UserService;
import com.example.ByteForge.user.stats.dto.response.CalendarActivityResponse;
import com.example.ByteForge.user.stats.repository.CalendarActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user/stats")
@RequiredArgsConstructor
public class CalendarActivityController {

    private final CalendarActivityRepository calendarActivityRepository;
    private final UserService userService;

    @GetMapping("/{userId}/calendar")
    public ResponseEntity<List<CalendarActivityResponse>> getUserCalendar(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year) {

        return _getUserCalendar(userId, year);
    }

    @GetMapping("/me/calendar")
    public ResponseEntity<List<CalendarActivityResponse>> getUserCalendarMe(@RequestParam(required = false) Integer year) {
        return _getUserCalendar(userService.getCurrentUserDetails().getId(), year);
    }


    private ResponseEntity<List<CalendarActivityResponse>> _getUserCalendar(Long userId, Integer year) {
        LocalDate startDate;
        LocalDate endDate;

        if (year != null) {
            // User requested a specific year
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        } else {
            // Default behavior: Last 365 days
            endDate = LocalDate.now();
            startDate = endDate.minusDays(365); // Standard 1-year heatmap window
        }

        List<CalendarActivityResponse> response = calendarActivityRepository
                .findByUserIdAndActivityDateBetween(userId, startDate, endDate)
                .stream()
                .map(entity -> new CalendarActivityResponse(entity.getActivityDate(), entity.getSubmissionCount()))
                .toList();

        return ResponseEntity.ok(response);
    }
}