package com.joonhyeok.app.concert.application.dto;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.Seat;

import java.util.List;

public record SeatsQueryResult(
        List<Seat> availableSeats,
        List<Seat> unavailableSeats
) {
    public SeatsQueryResult(Concert concert, Long dateId) {
        this(concert.getAvailableSeatByPerformanceDate(dateId), concert.getUnavailableSeatByPerformanceDate(dateId));
    }

}
