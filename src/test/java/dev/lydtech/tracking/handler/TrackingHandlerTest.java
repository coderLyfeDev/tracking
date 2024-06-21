package dev.lydtech.tracking.handler;

import dev.lydtech.tracking.message.DispatchCompleted;
import dev.lydtech.tracking.message.DispatchPrepared;
import dev.lydtech.tracking.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;

class TrackingHandlerTest {

    private TrackingHandler handler;
    private TrackingService trackingServiceMock;


    @BeforeEach
    void setUp() {
        trackingServiceMock = mock(TrackingService.class);
        handler = new TrackingHandler(trackingServiceMock);
    }

    @Test
    void listen_success() throws Exception{
        DispatchPrepared dispatchPrepared = new DispatchPrepared(randomUUID());
        handler.listen(dispatchPrepared);
        verify(trackingServiceMock, times(1)).process(dispatchPrepared);
    }

    @Test
    void listenCompleted_success() throws Exception{
        DispatchCompleted dispatchCompleted = new DispatchCompleted(randomUUID(), LocalDate.now().toString());
        handler.listen(dispatchCompleted);
        verify(trackingServiceMock, times(1)).processCompleted(dispatchCompleted);
    }

    @Test
    void listen_throwsException() throws Exception{
        DispatchPrepared dispatchPrepared = new DispatchPrepared(randomUUID());
        doThrow(new RuntimeException("process failed")).when(trackingServiceMock).process(dispatchPrepared);
        handler.listen(dispatchPrepared);
        verify(trackingServiceMock, times(1)).process(dispatchPrepared);
    }

    @Test
    void listenCompleted_throwsException() throws Exception{
        DispatchCompleted dispatchCompleted = new DispatchCompleted(randomUUID(), LocalDate.now().toString());
        doThrow(new RuntimeException("process failed")).when(trackingServiceMock).processCompleted(dispatchCompleted);
        handler.listen(dispatchCompleted);
        verify(trackingServiceMock, times(1)).processCompleted(dispatchCompleted);
    }
}