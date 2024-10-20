package com.joonhyeok.app.concert.domain;

import java.util.Optional;

public interface ConcertRepository {
    Concert save(Concert concert);
    Optional<Concert> findById(Long id);
}
