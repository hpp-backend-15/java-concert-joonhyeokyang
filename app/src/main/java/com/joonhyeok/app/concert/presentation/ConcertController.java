package com.joonhyeok.app.concert.presentation;

import com.joonhyeok.app.common.aop.token.VerifyWait;
import com.joonhyeok.app.concert.application.ConcertService;
import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.application.dto.AvailableSeatsByDateQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.application.dto.SeatsQueryResult;
import com.joonhyeok.openapi.apis.ConcertApi;
import com.joonhyeok.openapi.models.FindConcertAvailableSeatsResponse;
import com.joonhyeok.openapi.models.FindConcertPerformanceDatesResponse;
import com.joonhyeok.openapi.models.PerformanceDateResponse;
import com.joonhyeok.openapi.models.SeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConcertController implements ConcertApi {
    private final ConcertService concertService;

    @Override
    @VerifyWait
    public ResponseEntity<FindConcertPerformanceDatesResponse> findConcertPerformanceDates(
            @RequestHeader("Wait-Token") String waitToken,
            @PathVariable(value = "concertId") Long concertId
    ) {
        PerformanceDatesQueryResult result =
                concertService.queryPerformanceDates(new AvailablePerformanceDatesQuery(concertId));

        FindConcertPerformanceDatesResponse response = new FindConcertPerformanceDatesResponse()
                .availablePerformanceDates(result.availableDates().stream().map(pd -> new PerformanceDateResponse().date(pd.getPerformanceDate())).toList())
                .unavailablePerformanceDates(result.unavailableDates().stream().map(pd -> new PerformanceDateResponse().date(pd.getPerformanceDate())).toList());

        return ResponseEntity.ok(response);
    }

    @Override
    @VerifyWait
    public ResponseEntity<FindConcertAvailableSeatsResponse> findConcertPerformanceDatesSeats(
            @RequestHeader("Wait-Token") String waitToken,
            @PathVariable(value = "concertId") Long concertId,
            @PathVariable(value = "performanceDateId") Long performanceDateId
    ) {
        SeatsQueryResult result = concertService.querySeatsByDate(new AvailableSeatsByDateQuery(concertId, performanceDateId));
        FindConcertAvailableSeatsResponse response = new FindConcertAvailableSeatsResponse()
                .availableSeats(result.availableSeats().stream().map(
                        seat -> new SeatResponse()
                                .id(seat.getId())
                                .status(SeatResponse.StatusEnum.fromValue(seat.getStatus().toString()))
                ).toList())
                .unavailableSeats(
                        result.unavailableSeats().stream().map(
                                seat -> new SeatResponse()
                                        .id(seat.getId())
                                        .status(SeatResponse.StatusEnum.fromValue(seat.getStatus().toString()))
                        ).toList());
        return ResponseEntity.ok(response);
    }
}
