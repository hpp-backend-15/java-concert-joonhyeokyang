package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.pay.PayEvent;
import com.joonhyeok.app.user.domain.pay.PayEventPublisher;

public class DummyEventPublisher implements PayEventPublisher {

    @Override
    public void publish(PayEvent payEvent) {

    }
}
