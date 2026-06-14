package com.example.ByteForge.user.stats.dto.response;

import java.time.LocalDate;

public record CalendarActivityResponse(
        LocalDate date,
        int count
) {}