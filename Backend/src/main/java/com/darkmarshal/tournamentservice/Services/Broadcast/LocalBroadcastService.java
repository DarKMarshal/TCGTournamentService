package com.darkmarshal.tournamentservice.Services.Broadcast;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Local profile implementation: sends messages directly via the in-memory SimpleBroker.
 */
@Service
@Profile("local")
public class LocalBroadcastService implements WebSocketBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public LocalBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcast(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}
