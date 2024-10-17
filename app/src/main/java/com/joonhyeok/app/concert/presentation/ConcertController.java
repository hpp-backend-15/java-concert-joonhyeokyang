package com.joonhyeok.app.concert.presentation;

import com.joonhyeok.openapi.apis.ConcertApi;
import com.joonhyeok.openapi.models.FindConcertAvailableSeatsResponse;
import com.joonhyeok.openapi.models.FindConcertPerformanceDatesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concert")
public class ConcertController implements ConcertApi {

    @Override
    @GetMapping("/{concertId}/performanceDates")
    public ResponseEntity<FindConcertPerformanceDatesResponse> findConcertPerformanceDates(
            String waitToken,
            @PathVariable(value = "concertId") String concertId
    ) {
        return null;
    }

    @Override
    @GetMapping("/{concertId}/performanceDates/{performanceDateId}/seats")
    public ResponseEntity<FindConcertAvailableSeatsResponse> findConcertPerformanceDatesSeats(
            String waitToken,
            @PathVariable(value = "concertId") String concertId,
            @PathVariable(value = "performanceDateId") String performanceDateId
    ) {
        return null;
    }
}
