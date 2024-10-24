package com.joonhyeok.app.reservation.domain;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.joonhyeok.app.reservation.domain.ReservationStatus.PAYED;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Reservation {
    @Id
    @GeneratedValue
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
    }

    public void invalidate() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void confirmPay() {
        if (!isPayable()) {
            throw new IllegalStateException("결제할 수 없는 예약입니다");
        }
        this.status = PAYED;
    }

    public boolean isPayable() {
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
