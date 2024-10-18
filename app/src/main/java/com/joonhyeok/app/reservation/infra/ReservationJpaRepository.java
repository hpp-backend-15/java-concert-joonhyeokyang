package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import org.springframework.data.repository.Repository;

public interface ReservationJpaRepository extends ReservationRepository, Repository<Reservation, Long> {
}
