package com.joonhyeok.app.common.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joonhyeok.app.user.domain.pay.PayEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@Slf4j
public class PayEventDeserializer
        implements Deserializer<PayEvent> {

    @Override
    public PayEvent deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        PayEvent payEvent = null;
        try {
            payEvent = mapper.readValue(data, PayEvent.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Can not parse pay event, e = " + e.getMessage());
        }
        return payEvent;
    }
}
