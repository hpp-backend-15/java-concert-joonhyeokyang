package com.joonhyeok.app.user.infra.application;

import com.joonhyeok.app.user.application.PayInfoService;
import com.joonhyeok.app.user.application.dto.UserPayResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayInfoAPIService implements PayInfoService {
    public void sendPayInfo(UserPayResult result) {
        log.info("Sending Pay Results... = {}", result);
    }
}
