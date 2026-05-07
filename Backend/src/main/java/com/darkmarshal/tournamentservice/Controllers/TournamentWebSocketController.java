package com.darkmarshal.tournamentservice.Controllers;

import com.darkmarshal.tournamentservice.DTO.Event.EventDetailsDTO;
import com.darkmarshal.tournamentservice.DTO.Event.EventSummaryDTO;
import com.darkmarshal.tournamentservice.Services.CachedDataService;
import com.darkmarshal.tournamentservice.DTO.Event.EventDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TournamentWebSocketController {

    private final CachedDataService cachedDataService;

    @Autowired
    public TournamentWebSocketController(CachedDataService cachedDataService) {
        this.cachedDataService = cachedDataService;
    }

    // ── WebSocket endpoints ──────────────────────────────────────

    /**
     * Client sends to /app/events (empty body) → receives list on /topic/events
     */
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public List<EventSummaryDTO> getAllEventsWs() {
        return cachedDataService.getAllEventSummaries();
    }

    /**
     * Client sends { "eventId": "xxx" } to /app/event/details
     * → receives full event with divisions + results on /topic/event/details
     */
    @MessageMapping("/event/details")
    @SendTo("/topic/event/details")
    public EventDetailsDTO getEventDetailsWs(EventDetailsRequest request) {
        return cachedDataService.getEventDetails(request.getEventId());
    }

    // ── REST endpoints ───────────────────────────────────────────

    @GetMapping("/api/events")
    @ResponseBody
    public ResponseEntity<List<EventSummaryDTO>> getAllEvents() {
        return ResponseEntity.ok(cachedDataService.getAllEventSummaries());
    }

    @GetMapping("/api/events/{eventId}")
    @ResponseBody
    public ResponseEntity<EventDetailsDTO> getEventDetails(@PathVariable String eventId) {
        EventDetailsDTO details = cachedDataService.getEventDetails(eventId);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}
