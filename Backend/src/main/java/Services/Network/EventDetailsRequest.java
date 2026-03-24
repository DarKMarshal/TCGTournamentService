package Services.Network;

/**
 * Request payload for requesting details of a specific event over STOMP.
 */
public class EventDetailsRequest {
    private String eventId;

    public EventDetailsRequest() {}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
