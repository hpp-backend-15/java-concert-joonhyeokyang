package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;

import java.util.HashMap;
import java.util.Optional;

public class SeatMemoryRepository implements SeatRepository {
    private HashMap<Long, Seat> map = new HashMap<>();
    private Long id = 0L;

    public Optional<Seat> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public Seat save(Seat seat) {
        Seat newSeat = new Seat(id, seat.getStatus(), seat.getLastReservedAt(), seat.getVersion());
        map.put(id, newSeat);
        id = id + 1;
        return newSeat;
    }

    @Override
    public Optional<Seat> findByIdWithLock(Long seatId) {
        return findById(seatId);
    }
}
