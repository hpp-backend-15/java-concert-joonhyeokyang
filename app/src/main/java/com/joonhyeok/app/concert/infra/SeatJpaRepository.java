package com.joonhyeok.app.concert.infra;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import org.springframework.data.repository.Repository;

public interface SeatJpaRepository extends SeatRepository, Repository<Seat, Long> {
}
