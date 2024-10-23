package com.joonhyeok.app.reservation.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
    List<Reservation> findAllByStatusAndCreatedAtAfter(ReservationStatus status, LocalDateTime createdAt);
}
