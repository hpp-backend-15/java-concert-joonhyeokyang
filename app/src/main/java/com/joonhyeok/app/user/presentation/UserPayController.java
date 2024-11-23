package com.joonhyeok.app.user.presentation;

import com.joonhyeok.app.user.application.UserPayService;
import com.joonhyeok.app.user.application.dto.user.UserPayCommand;
import com.joonhyeok.app.user.application.dto.user.UserPayResult;
import com.joonhyeok.openapi.apis.PayApi;
import com.joonhyeok.openapi.models.PayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserPayController implements PayApi {
    private final UserPayService userPayService;

    @Override
    public ResponseEntity<PayResponse> payUserReservation(Long userId, Long reservationId) {
        UserPayCommand command = new UserPayCommand(userId, reservationId);
        UserPayResult result = userPayService.pay(command);
        PayResponse response = new PayResponse().reservationId(result.reservationId());
        return ResponseEntity.ok(response);
    }
}
