package com.joonhyeok.app.user.domain;

public interface PayEventPublisher {
    public void publish(PayEvent payEvent);
}
