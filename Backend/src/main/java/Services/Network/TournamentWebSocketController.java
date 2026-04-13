package Services.Network;

import Models.Event;
import Models.Result;
import Services.Contracts.IEventRepository;
import Services.Contracts.IResultsRepository;
import Services.Contracts.ITournamentRepository;
import Services.DTO.Event.DivisionDTO;
import Services.DTO.Event.EventDetailsDTO;
import Services.DTO.Event.EventSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(originPatterns = "*")
public class TournamentWebSocketController {

    private final IEventRepository eventRepository;
    private final ITournamentRepository tournamentRepository;
    private final IResultsRepository resultsRepository;

    @Autowired
    public TournamentWebSocketController(
            IEventRepository eventRepository,
            ITournamentRepository tournamentRepository,
            IResultsRepository resultsRepository
    ) {
        this.eventRepository = eventRepository;
        this.tournamentRepository = tournamentRepository;
        this.resultsRepository = resultsRepository;
    }

    // ── WebSocket endpoints ──────────────────────────────────

    /**
     * Client sends to /app/events (empty body) → receives list on /topic/events
     */
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public List<EventSummaryDTO> getAllEventsWs() {
        return eventRepository.getAllEvents().stream()
                .map(e -> new EventSummaryDTO(e.getId(), e.getName()))
                .toList();
    }

    /**
     * Client sends { "eventId": "xxx" } to /app/event/details
     * → receives full event with divisions + results on /topic/event/details
     */
    @MessageMapping("/event/details")
    @SendTo("/topic/event/details")
    public EventDetailsDTO getEventDetailsWs(EventDetailsRequest request) {
        String eventId = request.getEventId();

        Event event = eventRepository.getAllEvents().stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElse(null);

        if (event == null) return null;

        return new EventDetailsDTO(event.getId(), event.getName(), buildDivisions(eventId));
    }

    // ── REST endpoints ───────────────────────────────────────

    @GetMapping("/api/events")
    @ResponseBody
    public ResponseEntity<List<EventSummaryDTO>> getAllEvents() {
        List<EventSummaryDTO> events = eventRepository.getAllEvents().stream()
                .map(e -> {
                    Map<String, String> winners = findWinners(e.getId());
                    return new EventSummaryDTO(e.getId(), e.getName(), winners);
                })
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/api/events/{eventId}")
    @ResponseBody
    public ResponseEntity<EventDetailsDTO> getEventDetails(@PathVariable String eventId) {
        var event = eventRepository.getEventById(eventId);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new EventDetailsDTO(event.getId(), event.getName(), buildDivisions(eventId)));
    }

    // ── Shared helpers ───────────────────────────────────────

    private List<DivisionDTO> buildDivisions(String eventId) {
        return tournamentRepository.findAllDivisions(eventId).stream()
                .map(div -> {
                    String ageDivision = div[0];
                    String tournamentType = div[1];
                    List<Result> results = resultsRepository.getResultsByEventAndDivision(eventId, ageDivision);
                    return new DivisionDTO(ageDivision, tournamentType, results);
                })
                .toList();
    }

    private Map<String, String> findWinners(String eventId) {
        Map<String, String> winners = new LinkedHashMap<>();
        List<String[]> divisions = tournamentRepository.findAllDivisions(eventId);
        for (String[] div : divisions) {
            List<Result> results = resultsRepository.getResultsByEventAndDivision(eventId, div[0]);
            if (!results.isEmpty()) {
                winners.put(div[0], results.getFirst().getPlayer().getName());
            }
        }
        return winners;
    }
}
