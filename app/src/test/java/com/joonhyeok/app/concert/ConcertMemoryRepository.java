package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.concert.domain.PerformanceDate;
import com.joonhyeok.app.concert.domain.SeatRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ConcertMemoryRepository implements ConcertRepository {
    private final SeatRepository seatRepository;
    private HashMap<Long, Concert> map = new HashMap<>();
    private Long id = 1L;

    public ConcertMemoryRepository(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Optional<Concert> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public Concert save(Concert concert) {
        Concert newConcert = new Concert(id, concert.getPerformer(), concert.getPerformanceDateList());
        //Save Seats CASCADE Persists
        concert.getPerformanceDateList().stream()
                .flatMap(performanceDate -> performanceDate.getSeatList().stream())
                .forEach(seatRepository::save);

        map.put(id, newConcert);
        id = id + 1;
        return newConcert;
    }

}
