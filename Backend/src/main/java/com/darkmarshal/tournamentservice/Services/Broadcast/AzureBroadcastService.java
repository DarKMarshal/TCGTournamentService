package com.darkmarshal.tournamentservice.Services.Broadcast;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Azure profile implementation: publishes messages through Azure Service Bus
 * so all application instances can relay them to their connected WebSocket clients.
 */
@Service
@Profile("azure")
public class AzureBroadcastService implements WebSocketBroadcastService {

    private final AzureServiceBusRelay relay;

    public AzureBroadcastService(AzureServiceBusRelay relay) {
        this.relay = relay;
    }

    @Override
    public void broadcast(String destination, Object payload) {
        relay.publish(destination, payload);
    }
}
