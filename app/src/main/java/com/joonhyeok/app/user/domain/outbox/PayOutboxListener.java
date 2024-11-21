package com.joonhyeok.app.user.domain.outbox;

import com.joonhyeok.app.user.domain.pay.PayEvent;

public interface PayOutboxListener {
    public void listen(PayEvent payEvent);
}
