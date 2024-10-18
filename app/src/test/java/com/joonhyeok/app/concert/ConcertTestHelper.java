package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.PerformanceDate;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트코드 작성을 위하여 만든 헬퍼 클래스
 * 좌석 리스트와 오늘, 내일, 모레 공연을 갖는 콘서트를 만든다
 * 오늘, 내일, 모레 3일 공연일자를 만든다
 *
 * @return
 */
public class ConcertTestHelper {
    public static Concert createConcertWithAvailableSeats() {
        List<PerformanceDate> performanceDates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PerformanceDate performanceDate = new PerformanceDate(null, LocalDate.now().plusDays(i), createAllAvailableSeats());
            performanceDates.add(performanceDate);
        }
        return new Concert(0L, "joonhyeok", performanceDates);
    }

    public static Concert createConcertWithUnavailableSeats() {
        List<PerformanceDate> performanceDates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PerformanceDate performanceDate = new PerformanceDate(null, LocalDate.now().plusDays(i), createAllUnavailableSeats());
            performanceDates.add(performanceDate);
        }
        return new Concert(0L, "joonhyeok", performanceDates);
    }

    public static List<Seat> createAllAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat(null, SeatStatus.AVAILABLE, null, 0);
            seats.add(seat);
        }
        return seats;
    }

    public static List<Seat> createAllUnavailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat(null, SeatStatus.UNAVAILABLE, null, 0);
            seats.add(seat);
        }
        return seats;
    }
}
