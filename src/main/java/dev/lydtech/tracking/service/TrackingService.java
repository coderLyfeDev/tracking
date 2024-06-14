package dev.lydtech.tracking.service;

import dev.lydtech.tracking.message.DispatchPrepared;
import dev.lydtech.tracking.message.TrackingStatusUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {
    private static final String TRACKING_STATUS_TOPIC = "tracking.status";
    private final KafkaTemplate<String,Object> kafkaProducer;
    public void process(DispatchPrepared dispatchPrepared) throws Exception{
        TrackingStatusUpdated trackingStatusUpdated = TrackingStatusUpdated.builder()
                        .orderID(dispatchPrepared.getOrderId())
                                .status("PREPARING")
                                        .build();

        kafkaProducer.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated).get();
    }
}
