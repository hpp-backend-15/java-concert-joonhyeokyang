package com.joonhyeok.app.concert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.joonhyeok.app.concert.domain.SeatStatus.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class PerformanceDate {
    @Id
    @GeneratedValue
    @Column(name = "performance_dates_id")
    private Long id;

    @Column(name = "performance_dates_dates")
    LocalDate performanceDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_dates_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<Seat> seatList = new ArrayList<>();

    public boolean checkAvailableSeat() {
        return seatList.stream().anyMatch(seat -> seat.getStatus().equals(AVAILABLE));
    }

    public boolean checkUnAvailableSeat() {
        return seatList.stream().anyMatch(seat -> !seat.getStatus().equals(AVAILABLE));
    }
}