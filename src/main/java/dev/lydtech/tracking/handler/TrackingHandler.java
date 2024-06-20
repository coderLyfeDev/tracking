package dev.lydtech.tracking.handler;

import dev.lydtech.tracking.message.DispatchPrepared;
import dev.lydtech.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
        id =  "orderTrackingClient",
        topics = "dispatch.tracking",
        groupId = "tracking.order.status.consumer",
        containerFactory = "kafkaListenerContainerFactory"
)
public class TrackingHandler {
    private final TrackingService trackingService;

    @KafkaHandler
    public void listen(DispatchPrepared payload){
        log.info("Message received: "+ payload);
        try {
            trackingService.process(payload);
        } catch (Exception e){
            log.error("Processing failure", e);
        }
    }
}
