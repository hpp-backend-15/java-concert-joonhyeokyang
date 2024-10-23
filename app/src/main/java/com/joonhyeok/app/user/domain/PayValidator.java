package com.joonhyeok.app.user.domain;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.reservation.domain.Reservation;

public interface PayValidator {
    public boolean validate(User user, Seat seat, Reservation reservation);
}
