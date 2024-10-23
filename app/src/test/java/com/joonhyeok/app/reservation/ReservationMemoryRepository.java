package com.joonhyeok.app.reservation;

import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationMemoryRepository implements ReservationRepository {
    private HashMap<Long, Reservation> map = new HashMap<>();
    private Long id = 1L;

    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public Reservation save(Reservation reservation) {
        Reservation newReservation = new Reservation(id, reservation.getStatus(), reservation.getSeatId(), reservation.getUserId(), reservation.getCreatedAt(), reservation.getModifiedAt());
        map.put(id, newReservation);
        id = id + 1;
        return newReservation;
    }

    @Override
    public List<Reservation> findAllByStatusAndCreatedAtAfter(ReservationStatus status, LocalDateTime createdAt) {
        return map.values().stream()
                .filter(reservation -> reservation.getStatus().equals(status))
                .filter(reservation -> reservation.getCreatedAt().isAfter(createdAt))
                .toList();
    }
}
