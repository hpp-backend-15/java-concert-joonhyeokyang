package com.joonhyeok.app.concert.application;

import com.joonhyeok.app.concert.application.dto.AvailableSeatsByDateQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.concert.application.dto.SeatsQueryResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class
ConcertService {
    private final ConcertRepository concertRepository;

    public PerformanceDatesQueryResult queryPerformanceDates(AvailablePerformanceDatesQuery query) {
        Long concertId = query.concertId();
        Concert concert = concertRepository.findAvailableConcertById(concertId)
                .orElseThrow(EntityNotFoundException::new);
        return new PerformanceDatesQueryResult(concert);
    }

    public SeatsQueryResult querySeatsByDate(AvailableSeatsByDateQuery query) {
        Long concertId = query.concertId();
        Long performanceDateId = query.performanceDateId();
        Concert concert = concertRepository.findAvailableConcertById(concertId)
                .orElseThrow(EntityNotFoundException::new);
        return new SeatsQueryResult(concert, performanceDateId);
    }
}
