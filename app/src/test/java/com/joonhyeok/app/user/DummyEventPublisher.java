package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.PayEvent;
import com.joonhyeok.app.user.domain.PayEventPublisher;

public class DummyEventPublisher implements PayEventPublisher {

    @Override
    public void publish(PayEvent payEvent) {

    }
}
