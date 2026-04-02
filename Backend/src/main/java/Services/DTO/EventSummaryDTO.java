package Services.DTO;

import java.util.Map;

/**
 * Lightweight event summary for the event picker list.
 */
public record EventSummaryDTO(String id, String name, Map<String, String> winners) {
    public EventSummaryDTO(String id, String name) {
        this(id, name, Map.of());
    }
}
