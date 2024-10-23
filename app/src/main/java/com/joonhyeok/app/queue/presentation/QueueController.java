package com.joonhyeok.app.queue.presentation;

import com.joonhyeok.app.queue.appication.QueueService;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import com.joonhyeok.openapi.apis.QueueApi;
import com.joonhyeok.openapi.models.QueueResponse;
import com.joonhyeok.openapi.models.RegisterQueueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QueueController implements QueueApi {
    private final QueueService queueService;


    @Override
    public ResponseEntity<QueueResponse> getRegisteredQueue(String waitToken) {
        QueueQueryResult result = queueService.query(QueueQuery.from(waitToken));
        QueueResponse response = new QueueResponse()
                .userId(result.userId())
                .positionInQueue(result.position())
                .waitId(result.waitId())
                .status(result.status().name());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Void> registerQueue(RegisterQueueRequest request) {
        Long userId = request.getUserId();
        EnqueueResult result = queueService.enqueue(new EnqueueCommand(userId));
        return ResponseEntity.status(HttpStatus.CREATED).header("Wait-Token", result.waitId()).body(null);
    }
}
