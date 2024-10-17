package com.joonhyeok.app.concert.infra;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.concert.domain.PerformanceDate;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ConcertMemoryRepository implements ConcertRepository {
    private HashMap<Long, Concert> map = new HashMap<>();
    private Long id = 0L;

    public Optional<Concert> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public Concert save(Concert concert) {
        Concert newConcert = new Concert(id, concert.getPerformer(), concert.getPerformanceDateList());
        map.put(id, newConcert);
        id = id + 1;
        return newConcert;
    }

    @Override
    public Optional<Concert> findAvailableConcertById(Long id) {
        Concert value = map.get(id);
        List<PerformanceDate> performanceDateList = value.getPerformanceDateList();
        boolean availability = performanceDateList.stream()
                .anyMatch(PerformanceDate::checkAvailableSeat);
        if (!availability) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(value);
        }
    }
}
