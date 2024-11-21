package com.joonhyeok.app.user.domain.pay;

public interface PayEventListener {
    public void sendPayInfo(PayEvent payEvent);
}
