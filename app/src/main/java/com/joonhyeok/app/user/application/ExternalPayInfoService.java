package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.application.dto.UserPayResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExternalPayInfoService {
    public void sendPayInfo(UserPayResult result) {
        log.info("Sending Pay Results... = {}", result);
    }
}
