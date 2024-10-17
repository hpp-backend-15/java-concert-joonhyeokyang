package com.joonhyeok.app.concert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.joonhyeok.app.concert.domain.SeatStatus.AVAILABLE;
import static com.joonhyeok.app.concert.domain.SeatStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Seat {
    @Id
    @GeneratedValue
    @Column(name = "seats_id")
    private Long id;

    @Enumerated(STRING)
    @Column(name = "seats_status")
    private SeatStatus status;

    LocalDateTime lastReservedAt;

    @Version
    private Integer version;

    public boolean isReservable() {
        return AVAILABLE == status;
    }

    public void reserveSeat() {
        if (!isReservable()) {
            throw new IllegalStateException("이미 선택된 좌석입니다.");
        }
        this.status = PENDING;
    }
}
