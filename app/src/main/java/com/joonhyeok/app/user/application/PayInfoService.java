package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.application.dto.UserPayResult;
import com.joonhyeok.app.user.domain.PayEvent;

public interface PayInfoService {
     void sendPayInfo(PayEvent payEvent);
}
