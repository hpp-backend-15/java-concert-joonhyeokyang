package com.joonhyeok.app.queue.presentation;

import com.joonhyeok.app.common.aop.token.VerifyWait;
import com.joonhyeok.openapi.apis.QueueApi;
import com.joonhyeok.openapi.models.QueueResponse;
import com.joonhyeok.openapi.models.RegisterQueueRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueueController implements QueueApi {

    @Override
    @VerifyWait
    public ResponseEntity<QueueResponse> getRegisteredQueue(String waitToken) {
        return ResponseEntity.status(HttpStatus.OK).body(new QueueResponse());
    }

    @Override
    @VerifyWait
    public ResponseEntity<Void> registerQueue(RegisterQueueRequest registerQueueRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
