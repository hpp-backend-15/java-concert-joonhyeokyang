package com.joonhyeok.app.concert.application;

import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.application.dto.AvailableSeatsByDateQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.application.dto.SeatsQueryResult;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
    private final ConcertRepository concertRepository;

    @Cacheable(
            value = "performanceDates",
            key = "'concertId-'+#query.concertId()",
            cacheManager = "contentCacheManager"
    )
    public PerformanceDatesQueryResult queryPerformanceDates(AvailablePerformanceDatesQuery query) {
        Long concertId = query.concertId();
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(EntityNotFoundException::new);
        return new PerformanceDatesQueryResult(concert);
    }

    @Cacheable(
            value = "SeatsByDate",
            key = "'concertId-'+#query.concertId()+'-performanceDateId-'+#query.performanceDateId()",
            cacheManager = "contentCacheManager"
    )
    public SeatsQueryResult querySeatsByDate(AvailableSeatsByDateQuery query) {
        Long concertId = query.concertId();
        Long performanceDateId = query.performanceDateId();
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(EntityNotFoundException::new);
        return new SeatsQueryResult(concert, performanceDateId);
    }
}
