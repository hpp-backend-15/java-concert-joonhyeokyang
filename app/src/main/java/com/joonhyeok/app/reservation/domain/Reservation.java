package com.joonhyeok.app.reservation.domain;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.user.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.joonhyeok.app.reservation.domain.ReservationStatus.PAYED;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservations_id")
    private Long id;

    @Enumerated(STRING)
    @Column(nullable = false, name = "reservations_status")
    private ReservationStatus status;

    @Column(nullable = false, updatable = false, name = "reservations_seat_id")
    private Long seatId;

    @Column(nullable = false, updatable = false, name = "reservations_user_id")
    private Long userId;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public Reservation(ReservationStatus status, Long seatId, Long userId) {
        setStatus(status);
        setSeatId(seatId);
        setUserId(userId);
        setCreatedAt(LocalDateTime.now());
        log.info("new Reservation seatId = {}, userId = {}", seatId, userId);
    }

    public void invalidate() {
        log.info("invalidate reservation reservationId = {}", this.id);
        this.status = ReservationStatus.CANCELLED;
    }

    public void confirmPay() {
        if (!isReservable()) {
            throw new IllegalStateException("결제할 수 없는 예약입니다");
        }
        log.info("pay confirmed reservationId = {}", this.id);
        this.status = PAYED;
    }

    public boolean isReservable() {
        return this.status == RESERVED;
    }

    public boolean isRightfullyReservedBy(User user) {
        return Objects.equals(this.userId, user.getId());
    }

    public boolean isRightfullyHolds(Seat seat) {
        return Objects.equals(this.seatId, seat.getId());
    }

    private void setStatus(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status가 없습니다");
        }
        this.status = status;
    }

    private void setSeatId(Long seatId) {
        if (seatId == null) {
            throw new IllegalArgumentException("seatId가 없습니다");
        }
        this.seatId = seatId;
    }

    private void setUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 없습니다");
        }
        this.userId = userId;
    }

    private void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt이 없습니다.");
        }
        this.createdAt = createdAt;
    }
}
