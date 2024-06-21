package dev.kafka.dispatch.handler;

import dev.kafka.dispatch.message.DispatchCompleted;
import dev.kafka.dispatch.message.DispatchPrepared;
import dev.kafka.dispatch.service.TrackingService;
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

    @KafkaHandler
    public void listen(DispatchCompleted payload){
        log.info("Message received: "+ payload);
        try {
            trackingService.processCompleted(payload);
        } catch (Exception e){
            log.error("Processing failure", e);
        }
    }
}
