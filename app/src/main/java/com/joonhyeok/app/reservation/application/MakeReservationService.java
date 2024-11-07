package com.joonhyeok.app.reservation.application;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakeReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "performanceDates", cacheNames = "concerts", key = "concertId-#command.concertId()", cacheManager = "contentCacheManager"),
            @CacheEvict(value = "SeatsByDate", cacheNames = "concerts", key = "concertId-#command.concertId()-performanceDateId-#command.performanceDateId()", cacheManager = "contentCacheManager")
    })
    public MakeReservationResult reserve(MakeReservationCommand command) {
        Long seatId = command.seatId();
        Long userId = command.userId();

        Seat lockedSeat = seatRepository.findWithLockById(seatId).orElseThrow(() -> new EntityNotFoundException("해당 좌석은 존재하지 않습니다. seatId = " + seatId));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저는 존재하지 않습니다. userId = " + userId));

        // TODO in new Reservation() -> Events.raise(new SeatReservationEvent) with Transaction
        lockedSeat.reserveSeat();
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, lockedSeat.getId(), user.getId());
        Reservation saved = reservationRepository.save(reservation);
        return new MakeReservationResult(saved.getId());
    }
}
