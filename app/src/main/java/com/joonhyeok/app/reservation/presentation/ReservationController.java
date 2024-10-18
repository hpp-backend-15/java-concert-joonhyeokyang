package com.joonhyeok.app.reservation.presentation;

import com.joonhyeok.app.common.aop.token.VerifyWait;
import com.joonhyeok.openapi.apis.ReservationApi;
import com.joonhyeok.openapi.models.ReservationResponse;
import com.joonhyeok.openapi.models.ReservationsPostRequest;
import org.springframework.http.ResponseEntity;

public class ReservationController implements ReservationApi {

    @Override
    @VerifyWait
    public ResponseEntity<ReservationResponse> reservationsPost(
            String waitToken,
            ReservationsPostRequest reservationsPostRequest
    ) {
        return ResponseEntity.ok(new ReservationResponse());
    }
}
