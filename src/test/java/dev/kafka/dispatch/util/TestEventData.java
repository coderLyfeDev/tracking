package dev.kafka.dispatch.util;

import dev.kafka.dispatch.message.DispatchCompleted;
import dev.kafka.dispatch.message.DispatchPrepared;
import java.util.UUID;

public class TestEventData {

    public static DispatchPrepared buildDispatchPrepared(UUID orderId){
        return DispatchPrepared.builder()
                .orderId(orderId)
                .build();
    }

    public static DispatchCompleted buildDispatchCompleted(UUID orderId, String date) {
        return DispatchCompleted.builder()
                .orderId(orderId)
                .date(date)
                .build();
    }
}
