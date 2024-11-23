package com.joonhyeok.app.user.infra.application;

import com.joonhyeok.app.user.application.PayInfoService;
import com.joonhyeok.app.user.domain.user.PayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayInfoAPIService implements PayInfoService {
    public void sendPayInfo(PayEvent payEvent) {
        log.info("Sending Pay Results... = {}", payEvent);
    }
}
