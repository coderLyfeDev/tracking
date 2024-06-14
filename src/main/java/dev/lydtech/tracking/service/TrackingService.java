package dev.lydtech.tracking.service;

import dev.lydtech.tracking.message.TrackingStatusUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {

    public void process(TrackingStatusUpdated trackingStatusUpdated){

    }
}
