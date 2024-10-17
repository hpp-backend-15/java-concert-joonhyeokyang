package com.joonhyeok.app.concert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
