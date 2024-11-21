package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.domain.pay.PayEvent;

public interface PayInfoService {
     void sendPayInfo(PayEvent payEvent);
}
