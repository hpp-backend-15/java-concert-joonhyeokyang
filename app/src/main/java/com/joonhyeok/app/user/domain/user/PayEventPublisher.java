package com.joonhyeok.app.user.domain.user;

public interface PayEventPublisher {
    public void publish(PayEvent payEvent);
}
