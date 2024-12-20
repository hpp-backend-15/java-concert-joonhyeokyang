package com.joonhyeok.app.concert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.joonhyeok.app.concert.domain.SeatStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Entity
@Table(name = "seats")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seats_id")
    private Long id;

    @Enumerated(STRING)
    @Column(name = "seats_status")
    private SeatStatus status;

    LocalDateTime lastReservedAt;

    @Column(name = "seats_price")
    Long price;

    @Version
    private Integer version;

    public boolean isReservable() {
        return this.status == AVAILABLE;
    }

    public boolean isAvailable() {
        return this.status == PENDING;
    }

    public void reserveSeat() {
        if (!isReservable()) {
            throw new IllegalStateException("이미 선택된 좌석입니다.");
        }
        log.info("reserve seat seatId = {}", this.id);
        this.status = PENDING;
    }

    public void paySeat() {
        if (!isAvailable()) {
            throw new IllegalStateException("결제할 수 없는 좌석입니다");
        }
        this.status = UNAVAILABLE;
    }
}
