package com.joonhyeok.app.concert.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "concerts")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concerts_id")
    private Long id;
    private String performer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "concerts_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<PerformanceDate> performanceDateList = new ArrayList<>();

    public List<PerformanceDate> getAvailablePerformanceDates() {
        return performanceDateList.stream()
                .filter(PerformanceDate::checkAvailableSeat)
                .toList();
    }

    public List<PerformanceDate> getUnAvailablePerformanceDates() {
        return performanceDateList.stream()
                .filter(PerformanceDate::checkUnAvailableSeat)
                .toList();
    }

    /**
     * 해당공연일의 모든 예약 가능한 좌석을 가져온다
     *
     * @param dateId
     * @return
     */
    public List<Seat> getAvailableSeatByPerformanceDate(Long dateId) {
        boolean hasPerformanceDate = performanceDateList.stream()
                .anyMatch(performanceDate -> performanceDate.getId().equals(dateId));
        if (!hasPerformanceDate) {
            throw new EntityNotFoundException("해당 performanceDate에 공연일자가 없습니다. performanceId = " + dateId);
        }
        return performanceDateList.stream()
                .filter(performanceDate -> performanceDate.getId().equals(dateId))
                .flatMap(performanceDate ->
                        performanceDate.getSeatList().stream()
                                .filter(seat -> seat.getStatus().equals(SeatStatus.AVAILABLE)))
                .toList();
    }

    /**
     * 해당공연일의 모든 예약 불가능한 좌석을 가져온다
     *
     * @param dateId
     * @return
     */
    public List<Seat> getUnavailableSeatByPerformanceDate(Long dateId) {
        boolean hasPerformanceDate = performanceDateList.stream()
                .anyMatch(performanceDate -> performanceDate.getId().equals(dateId));
        if (!hasPerformanceDate) {
            throw new EntityNotFoundException("해당 performanceDate에 공연일자가 없습니다. performanceId = " + dateId);
        }
        return performanceDateList.stream()
                .filter(performanceDate -> performanceDate.getId().equals(dateId))
                .flatMap(performanceDate ->
                        performanceDate.getSeatList().stream()
                                .filter(seat -> !seat.getStatus().equals(SeatStatus.AVAILABLE)))
                .toList();
    }
}
