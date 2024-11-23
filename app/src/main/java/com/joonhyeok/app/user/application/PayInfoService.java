package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.domain.user.PayEvent;

public interface PayInfoService {
     void sendPayInfo(PayEvent payEvent);
}
