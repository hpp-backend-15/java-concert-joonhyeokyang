package com.joonhyeok.app.concert.domain;

import java.util.Optional;

public interface ConcertRepository {
    Optional<Concert> findAvailableConcertById(Long id);
    Concert save(Concert concert);
}
