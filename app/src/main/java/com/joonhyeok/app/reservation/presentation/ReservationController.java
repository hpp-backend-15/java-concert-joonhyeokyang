package com.joonhyeok.app.reservation.presentation;

import com.joonhyeok.app.common.aop.token.VerifyWait;
import com.joonhyeok.app.reservation.application.MakeReservationService;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.openapi.apis.ReservationApi;
import com.joonhyeok.openapi.models.ReservationResponse;
import com.joonhyeok.openapi.models.ReservationsPostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class ReservationController implements ReservationApi {
    private final MakeReservationService makeReservationService;

    @Override
    @VerifyWait
    public ResponseEntity<ReservationResponse> reservationsPost(
            String waitToken,
            ReservationsPostRequest reservationsPostRequest
    ) {
        MakeReservationResult result = makeReservationService.reserve(
                new MakeReservationCommand(
                                reservationsPostRequest.getSeatId(),
                                reservationsPostRequest.getUserId())
        );
        return ResponseEntity.ok(new ReservationResponse().reservationId(result.reservationId()));
    }
}
