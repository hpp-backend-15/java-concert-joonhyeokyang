package com.joonhyeok.app.user.domain.pay;

public interface PayEventPublisher {
    public void publish(PayEvent payEvent);
}
