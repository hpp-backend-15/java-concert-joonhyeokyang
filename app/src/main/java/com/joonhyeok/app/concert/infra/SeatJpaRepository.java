package com.joonhyeok.app.concert.infra;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface SeatJpaRepository extends SeatRepository, JpaRepository<Seat, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Seat> findWithLockById(Long seatId);

}
