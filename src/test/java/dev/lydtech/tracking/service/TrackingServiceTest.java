package dev.lydtech.tracking.service;

import dev.lydtech.tracking.message.DispatchPrepared;
import dev.lydtech.tracking.message.TrackingStatusUpdated;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TrackingServiceTest {

    private TrackingService service;
    private KafkaTemplate kafkaProducerMock;

    @BeforeEach
    void setUp() {

        kafkaProducerMock = mock(KafkaTemplate.class);
        service = new TrackingService(kafkaProducerMock);
    }

    @Test
    void process() throws Exception{

        when(kafkaProducerMock.send(anyString(), ArgumentMatchers.any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));

        DispatchPrepared testEvent = TestEventData.buildDispatchPrepared(randomUUID());
        service.process(testEvent);
        verify(kafkaProducerMock, times(1)).send(eq("tracking.status"), ArgumentMatchers.any(TrackingStatusUpdated.class));
    }

    @Test
    void process_throwsException(){

        DispatchPrepared testEvent = TestEventData.buildDispatchPrepared(randomUUID());

        doThrow(new RuntimeException("Processing failure")).when(kafkaProducerMock).send(eq("tracking.status"), ArgumentMatchers.any(TrackingStatusUpdated.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.process(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq("tracking.status"), ArgumentMatchers.any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("Processing failure"));
    }
}