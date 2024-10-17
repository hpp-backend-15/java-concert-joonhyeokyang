package com.joonhyeok.app.reservation.domain;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.Seat;

public interface ReservationPolicy {
    boolean verify(Concert concert, Seat seat);
}
