package com.joonhyeok.app.concert.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트코드 작성을 위하여 만든 헬퍼 클래스
 * 좌석 리스트와 오늘, 내일, 모레 공연을 갖는 콘서트를 만든다
 */
public class ConcertTestHelper {
    public static  Concert createConcert(List<Seat> seatList) {
        List<PerformanceDate> performanceDates = createPerformanceDates(seatList);
        return new Concert(0L, "joonhyeok", performanceDates);
    }

    /**
     * 오늘, 내일, 모레 3일 공연일자를 만든다
     * @param seats
     * @return
     */
    public static List<PerformanceDate> createPerformanceDates(List<Seat> seats) {
        List<PerformanceDate> performanceDates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PerformanceDate performanceDate = new PerformanceDate((long) i, LocalDate.now().plusDays(i), seats);
            performanceDates.add(performanceDate);
        }
        return performanceDates;
    }

    public static List<Seat> createAllAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat((long) i, SeatStatus.AVAILABLE, null);
            seats.add(seat);
        }
        return seats;
    }

    public static List<Seat> createAllUnavailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat((long) i, SeatStatus.UNAVAILABLE, null);
            seats.add(seat);
        }
        return seats;
    }
}
