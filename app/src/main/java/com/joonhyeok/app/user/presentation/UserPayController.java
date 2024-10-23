package com.joonhyeok.app.user.presentation;

import com.joonhyeok.openapi.apis.PayApi;
import com.joonhyeok.openapi.models.PayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserPayController implements PayApi {
    @Override
    public ResponseEntity<PayResponse> payUserReservation(Long userId, Long reservationId) {
        return null;
    }
}
