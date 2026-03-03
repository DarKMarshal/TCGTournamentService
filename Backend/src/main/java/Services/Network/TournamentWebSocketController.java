package Services.Network;

import Database.Repositories.EventRepository;
import Models.Event;
import Models.Result;
import Services.Contracts.IResultsRepository;
import Services.Contracts.ITournamentRepository;
import Services.DTO.DivisionDTO;
import Services.DTO.EventDetailsDTO;
import Services.DTO.EventSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TournamentWebSocketController {

    private final EventRepository eventRepository;
    private final ITournamentRepository tournamentRepository;
    private final IResultsRepository resultsRepository;

    @Autowired
    public TournamentWebSocketController(
            EventRepository eventRepository,
            ITournamentRepository tournamentRepository,
            IResultsRepository resultsRepository
    ) {
        this.eventRepository = eventRepository;
        this.tournamentRepository = tournamentRepository;
        this.resultsRepository = resultsRepository;
    }

    /**
     * Client sends to /app/events (empty body) → receives list on /topic/events
     */
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public List<EventSummaryDTO> getAllEvents() {
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
    public EventDetailsDTO getEventDetails(EventDetailsRequest request) {
        String eventId = request.getEventId();

        // Find the event summary
        Event event = eventRepository.getAllEvents().stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElse(null);

        if (event == null) return null;

        // Build divisions with results from DB
        List<DivisionDTO> divisions = tournamentRepository.findAllDivisions(eventId).stream()
                .map(div -> {
                    String ageDivision = div[0];
                    String tournamentType = div[1];
                    List<Result> results = resultsRepository.getResultsByEventAndDivision(eventId, ageDivision);
                    return new DivisionDTO(ageDivision, tournamentType, results);
                })
                .toList();

        return new EventDetailsDTO(event.getId(), event.getName(), divisions);
    }
}
