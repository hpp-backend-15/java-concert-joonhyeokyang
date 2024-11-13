package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends ReservationRepository, JpaRepository<Reservation, Long> {
}
