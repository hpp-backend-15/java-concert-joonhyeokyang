package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.user.PayEvent;
import com.joonhyeok.app.user.domain.user.PayEventPublisher;

public class DummyEventPublisher implements PayEventPublisher {

    @Override
    public void publish(PayEvent payEvent) {

    }
}
