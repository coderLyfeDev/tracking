package dev.lydtech.tracking.integration;
import dev.lydtech.tracking.DispatchConfiguration;
import dev.lydtech.tracking.message.DispatchPrepared;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes={DispatchConfiguration.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(controlledShutdown = true)
@Slf4j
public class TrackingDispatchIntegrationTest {


    private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTestListener testListener;

    @Configuration
    static class TestConfig {

        @Bean
        public KafkaTestListener testListener() {
            return new KafkaTestListener();
        }
    }

    public static class KafkaTestListener{
        AtomicInteger dispatchTrackingCounter = new AtomicInteger(0);

        @KafkaListener(groupId = "KafkaIntegrationTest", topics = DISPATCH_TRACKING_TOPIC)
        void recievedDispatchTrackingRequest(@Payload DispatchPrepared payload){
            log.debug("Received request for dispatch status: "+payload);
            dispatchTrackingCounter.incrementAndGet();
        }
    }

    @BeforeEach
    public void setUp(){
        testListener.dispatchTrackingCounter.set(0);

        registry.getListenerContainers().stream().forEach(container -> ContainerTestUtils
                .waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    public void TrackingDispatchTest() throws Exception {

        DispatchPrepared dispatchPrepared = new DispatchPrepared(randomUUID());
        sendMessage(DISPATCH_TRACKING_TOPIC, dispatchPrepared);

        await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS).
                until(testListener.dispatchTrackingCounter:: get, equalTo(1));

    }

    private void sendMessage(String topic, Object data) throws Exception{
        kafkaTemplate.send(MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()).get();

    }
}




