package com.darkmarshal.tournamentservice.Services.Broadcast;


/**
 * Abstraction for broadcasting WebSocket messages.
 * <p>
 * On the <b>local</b> profile, messages are sent directly via {@link SimpMessagingTemplate}
 * (single instance, so in-memory is fine).
 * <p>
 * On the <b>azure</b> profile, messages are published through {@link AzureServiceBusRelay}
 * so every application instance can deliver them to its connected clients.
 */
public interface WebSocketBroadcastService {

    /**
     * Broadcast a message to all WebSocket subscribers of the given destination.
     *
     * @param destination STOMP destination (e.g. "/topic/events")
     * @param payload     the payload to send
     */
    void broadcast(String destination, Object payload);

    /**
     * Send a message to a specific user's queue.
     *
     * @param username    the target user's principal name
     * @param destination user destination (e.g. "/queue/upload-progress")
     * @param payload     the payload to send
     */
    void sendToUser(String username, String destination, Object payload);
}
