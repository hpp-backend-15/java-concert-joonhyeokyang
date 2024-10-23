package com.joonhyeok.app.user.infra;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.user.domain.PayValidator;
import com.joonhyeok.app.user.domain.User;
import org.springframework.stereotype.Service;

@Service
public class PayValidatorImpl implements PayValidator {

    @Override
    public boolean validate(User user, Seat seat, Reservation reservation) {
        if (!seat.isPayable()) {
            throw new IllegalStateException("결제 가능한 좌석 상태가 아닙니다. Status = " + seat.getStatus());
        }

        if (!reservation.isPayable()) {
            throw new IllegalStateException("결제 가능한 예약 상태가 아닙니다. Status = " + reservation.getStatus());
        }

        if (!user.isSufficientBalance(seat.getPrice())) {
            throw new IllegalStateException("잔액이 부족합니다. 충전해주세요 balance = " + user.getAccount().getBalance());
        }

        if (!reservation.isRightfullyReservedBy(user)) {
            throw new IllegalArgumentException("비정상적인 결제 시도입니다. 허재코치님 부르세요.");
        }

        if (!reservation.isRightfullyHolds(seat)) {
            throw new IllegalArgumentException("비정상적인 결제 시도입니다. 렌코치님 부르세요.");
        }

        return true;
    }

}
