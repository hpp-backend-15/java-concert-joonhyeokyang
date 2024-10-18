package com.joonhyeok.app.concert.application.dto;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.PerformanceDate;

import java.util.List;

public record PerformanceDatesQueryResult(
        List<PerformanceDate> availableDates,
        List<PerformanceDate> unavailableDates
) {
    public PerformanceDatesQueryResult(Concert concert) {
        this(concert.getAvailablePerformanceDates(), concert.getUnAvailablePerformanceDates());
    }
}
