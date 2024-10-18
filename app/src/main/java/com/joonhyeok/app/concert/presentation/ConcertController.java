package com.joonhyeok.app.concert.presentation;

import com.joonhyeok.openapi.apis.ConcertApi;
import com.joonhyeok.openapi.models.FindConcertAvailableSeatsResponse;
import com.joonhyeok.openapi.models.FindConcertPerformanceDatesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concert")
public class ConcertController implements ConcertApi {

    @Override
    @GetMapping("/{concertId}/performanceDates")
    public ResponseEntity<FindConcertPerformanceDatesResponse> findConcertPerformanceDates(
            @RequestHeader("Wait-Token") String waitToken,
            @PathVariable(value = "concertId") Long concertId
    ) {
        FindConcertPerformanceDatesResponse response = new FindConcertPerformanceDatesResponse();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{concertId}/performanceDates/{performanceDateId}/seats")
    public ResponseEntity<FindConcertAvailableSeatsResponse> findConcertPerformanceDatesSeats(
            @RequestHeader("Wait-Token") String waitToken,
            @PathVariable(value = "concertId") Long concertId,
            @PathVariable(value = "performanceDateId") Long performanceDateId
    ) {
        FindConcertAvailableSeatsResponse response = new FindConcertAvailableSeatsResponse();
        return ResponseEntity.ok(response);
    }
}
