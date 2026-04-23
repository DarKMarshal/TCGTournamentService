package com.darkmarshal.tournamentservice.Config.WebSocket;

import com.darkmarshal.tournamentservice.Services.Broadcast.AzureServiceBusRelay;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Azure profile: uses the in-memory SimpleBroker for WebSocket message delivery
 * to locally connected clients, combined with {@link AzureServiceBusRelay} which
 * forwards messages across all application instances via Azure Service Bus.
 * <p>
 * This two-layer approach works because:
 * <ul>
 *   <li>SimpleBroker efficiently pushes messages to WebSocket clients on this instance.</li>
 *   <li>AzureServiceBusRelay ensures that events published on one instance are
 *       replayed on every other instance's SimpleBroker, achieving cluster-wide delivery.</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
@Profile("azure")
public class AzureWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
