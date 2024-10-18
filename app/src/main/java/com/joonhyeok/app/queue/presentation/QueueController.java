package com.joonhyeok.app.queue.presentation;

import com.joonhyeok.openapi.apis.QueueApi;
import com.joonhyeok.openapi.models.QueueResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueueController implements QueueApi {
    @Override
    public ResponseEntity<QueueResponse> getRegisteredQueue(String waitToken) {
        return null;
    }

    @Override
    public ResponseEntity<Void> registerQueue() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
