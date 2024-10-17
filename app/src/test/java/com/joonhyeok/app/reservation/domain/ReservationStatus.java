package com.joonhyeok.app.reservation.domain;

public enum ReservationStatus {
    /**
     * RESERVED -> 예약 상태, 결제 대기 상태
     * PAYED -> 결제 완료 상태 (변경 불가능)
     * CANCELLED -> 예약, 결제 취소 상태
     */
    RESERVED, PAYED, CANCELLED;
}
