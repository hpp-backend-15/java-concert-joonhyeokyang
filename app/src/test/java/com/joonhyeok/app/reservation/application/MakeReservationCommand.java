package com.joonhyeok.app.reservation.application;

public record MakeReservationCommand(
        Long concertId,
        Long performanceDateId,
        Long seatId,
        Long userId
) {
}
