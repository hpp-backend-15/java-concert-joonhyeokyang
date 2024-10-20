package com.joonhyeok.app.concert.application.dto;

public record AvailableSeatsByDateQuery(
        Long concertId,
        Long performanceDateId
) {
}
