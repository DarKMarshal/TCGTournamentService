package com.darkmarshal.tournamentservice.Services.Broadcast;

import com.azure.messaging.servicebus.*;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Relays WebSocket broadcast messages across multiple application instances
 * via Azure Service Bus topics.
 * <p>
 * When a message needs to be broadcast (e.g., updated event list after upload),
 * call {@link #publish(String, Object)} instead of sending directly through
 * {@link SimpMessagingTemplate}. This component will:
 * <ol>
 *   <li>Publish the message to an Azure Service Bus topic.</li>
 *   <li>Every instance (including this one) receives the message via its own subscription.</li>
 *   <li>Each instance pushes the message to its locally connected WebSocket clients.</li>
 * </ol>
 * <p>
 * This ensures that clients connected to any instance receive the broadcast.
 */
@Component
@Profile("azure")
public class AzureServiceBusRelay {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.topic-name:websocket-relay}")
    private String topicName;

    /**
     * Unique ID for this application instance. Used to identify messages
     * that originated from this instance so they are not re-sent locally
     * (the originating instance already delivered the message via SimpMessagingTemplate).
     */
    private final String instanceId = UUID.randomUUID().toString();

    private ServiceBusSenderClient senderClient;
    private ServiceBusProcessorClient processorClient;

    public AzureServiceBusRelay(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void start() {
        // Sender — used by publish() to put messages onto the topic
        senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topicName)
                .buildClient();

        // Each instance gets its own subscription so every instance receives every message.
        // The subscription name includes the instanceId to create a per-instance subscription.
        String subscriptionName = "relay-" + instanceId.substring(0, 8);

        processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .processMessage(this::handleMessage)
                .processError(this::handleError)
                .buildProcessorClient();

        processorClient.start();
    }

    @PreDestroy
    public void stop() {
        if (processorClient != null) {
            processorClient.close();
        }
        if (senderClient != null) {
            senderClient.close();
        }
    }

    /**
     * Publishes a message to Azure Service Bus so all instances can relay it
     * to their locally connected WebSocket clients.
     *
     * @param destination the STOMP destination (e.g. "/topic/events")
     * @param payload     the message payload (will be serialized to JSON)
     */
    public void publish(String destination, Object payload) {
        try {
            RelayEnvelope envelope = new RelayEnvelope(instanceId, destination, null, objectMapper.writeValueAsString(payload));
            String json = objectMapper.writeValueAsString(envelope);
            senderClient.sendMessage(new ServiceBusMessage(json));
        } catch (Exception e) {
            System.err.println("Failed to publish to Service Bus: " + e.getMessage());
            // Fall back to local-only delivery so the current instance's clients still get the message
            messagingTemplate.convertAndSend(destination, payload);
        }
    }

    public void publishToUser(String username, String destination, Object payload) {
        try {
            RelayEnvelope envelope = new RelayEnvelope(instanceId, destination, username, objectMapper.writeValueAsString(payload));
            String json = objectMapper.writeValueAsString(envelope);
            senderClient.sendMessage(new ServiceBusMessage(json));
        } catch (Exception e) {
            System.err.println("Failed to publish user message to Service Bus: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(username, destination, payload);
        }
    }

    private void handleMessage(ServiceBusReceivedMessageContext context) {
        try {
            String body = context.getMessage().getBody().toString();
            RelayEnvelope envelope = objectMapper.readValue(body, RelayEnvelope.class);

            // Deliver the message to WebSocket clients connected to this instance
            if (envelope.targetUser() != null) {
                messagingTemplate.convertAndSendToUser(envelope.targetUser(), envelope.destination(), envelope.payload());
            } else {
                messagingTemplate.convertAndSend(envelope.destination(), envelope.payload());
            }
        } catch (Exception e) {
            System.err.println("Failed to process relayed message: " + e.getMessage());
        }
    }

    private void handleError(ServiceBusErrorContext context) {
        System.err.println("Service Bus relay error: " + context.getException().getMessage());
    }

    /**
     * Envelope that wraps a WebSocket broadcast for transit through Service Bus.
     */
    private record RelayEnvelope(String sourceInstanceId, String destination, String targetUser, String payload) {}
}
