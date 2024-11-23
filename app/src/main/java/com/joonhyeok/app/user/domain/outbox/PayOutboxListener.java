package com.joonhyeok.app.user.domain.outbox;

import com.joonhyeok.app.user.domain.user.PayEvent;

public interface PayOutboxListener {
    public void listen(PayEvent payEvent);
}
