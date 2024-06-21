package dev.kafka.dispatch.service;

import dev.kafka.dispatch.message.DispatchCompleted;
import dev.kafka.dispatch.message.DispatchPrepared;
import dev.kafka.dispatch.message.TrackingStatusUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {
    private static final String TRACKING_STATUS_TOPIC = "tracking.status";
    private final KafkaTemplate<String,Object> kafkaProducer;
    public void process(DispatchPrepared dispatchPrepared) throws Exception{
        log.info("Received message: "+dispatchPrepared);

        TrackingStatusUpdated trackingStatusUpdated = TrackingStatusUpdated.builder()
                        .orderID(dispatchPrepared.getOrderId())
                                .status("PREPARING")
                                        .build();
        kafkaProducer.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated).get();
    }

    public void processCompleted(DispatchCompleted dispatchCompleted) throws Exception{
        log.info("Received message: "+ dispatchCompleted);

        TrackingStatusUpdated trackingStatusUpdated = TrackingStatusUpdated.builder()
                .orderID(dispatchCompleted.getOrderId())
                .status("COMPLETED")
                .build();
        kafkaProducer.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated).get();
    }
}
