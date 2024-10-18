package com.joonhyeok.app.concert.domain;

import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long id);
    Seat save(Seat seat);

    Optional<Seat> findWithLockById(Long seatId);
}
