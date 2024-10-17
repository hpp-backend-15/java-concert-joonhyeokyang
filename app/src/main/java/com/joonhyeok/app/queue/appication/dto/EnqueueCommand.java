package com.joonhyeok.app.queue.appication.dto;

public record EnqueueCommand(
        String waitId
) {
    public static EnqueueCommand from(String waitId) {
        return new EnqueueCommand(waitId);
    }

}

