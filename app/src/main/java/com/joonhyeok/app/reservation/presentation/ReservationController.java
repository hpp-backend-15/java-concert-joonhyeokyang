package com.joonhyeok.app.reservation.presentation;

import com.joonhyeok.app.common.aop.token.VerifyWait;
import com.joonhyeok.app.common.lock.LockId;
import com.joonhyeok.app.common.lock.LockManager;
import com.joonhyeok.app.reservation.application.MakeReservationService;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.openapi.apis.ReservationApi;
import com.joonhyeok.openapi.models.MakeReservationRequest;
import com.joonhyeok.openapi.models.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {
    private final MakeReservationService makeReservationService;
    private final LockManager lockManager;


    @VerifyWait
    @Override
    public ResponseEntity<ReservationResponse> makeReservation(String waitToken, MakeReservationRequest request) {
        MakeReservationResult result = null;
//        LockId lock = null;
        result = makeReservationService.reserve(new MakeReservationCommand(request.getSeatId(), request.getUserId()));
//        try {
//            lock = lockManager.tryLock("seat", request.getSeatId());
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//        } finally {
//            lockManager.releaseLock(lock);
//        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationResponse().reservationId(result.reservationId()));
    }
}
