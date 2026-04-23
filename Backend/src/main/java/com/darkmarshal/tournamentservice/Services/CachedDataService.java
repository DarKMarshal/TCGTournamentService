package com.darkmarshal.tournamentservice.Services;

import com.darkmarshal.tournamentservice.Models.Event;
import com.darkmarshal.tournamentservice.Contracts.IEventRepository;
import com.darkmarshal.tournamentservice.Contracts.IPlayerRepository;
import com.darkmarshal.tournamentservice.Contracts.IResultsRepository;
import com.darkmarshal.tournamentservice.Contracts.ITournamentRepository;
import com.darkmarshal.tournamentservice.DTO.Event.EventSummaryDTO;
import com.darkmarshal.tournamentservice.DTO.Event.EventDetailsDTO;
import com.darkmarshal.tournamentservice.DTO.Event.DivisionDTO;
import com.darkmarshal.tournamentservice.DTO.Leaderboard.LeaderboardDTO;
import com.darkmarshal.tournamentservice.Models.AgeDivision;
import com.darkmarshal.tournamentservice.Models.Result;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer that wraps frequently-read repository calls with Spring Cache annotations.
 * <p>
 * Caches are automatically backed by in-memory storage (local profile)
 * or Redis (azure profile) depending on which CacheConfig is active.
 */
@Service
public class CachedDataService {

    private final IEventRepository eventRepository;
    private final ITournamentRepository tournamentRepository;
    private final IResultsRepository resultsRepository;
    private final IPlayerRepository playerRepository;

    public CachedDataService(
            IEventRepository eventRepository,
            ITournamentRepository tournamentRepository,
            IResultsRepository resultsRepository,
            IPlayerRepository playerRepository
    ) {
        this.eventRepository = eventRepository;
        this.tournamentRepository = tournamentRepository;
        this.resultsRepository = resultsRepository;
        this.playerRepository = playerRepository;
    }

    @Cacheable("events")
    public List<EventSummaryDTO> getAllEventSummaries() {
        return eventRepository.getAllEvents().stream()
                .map(e -> {
                    Map<String, String> winners = findWinners(e.getId());
                    return new EventSummaryDTO(e.getId(), e.getName(), winners);
                })
                .toList();
    }

    @Cacheable(value = "eventDetails", key = "#eventId")
    public EventDetailsDTO getEventDetails(String eventId) {
        Event event = eventRepository.getEventById(eventId);
        if (event == null) return null;

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

    @Cacheable("leaderboards")
    public List<LeaderboardDTO> getAllLeaderboards() {
        return playerRepository.getLeaderboards();
    }

    @Cacheable(value = "leaderboards", key = "#ageDivision.name()")
    public LeaderboardDTO getLeaderboardByAgeDivision(AgeDivision ageDivision) {
        return playerRepository.getLeaderboardByAgeDivision(ageDivision);
    }

    /**
     * Evicts all caches. Call this after data-changing operations (e.g. file upload/import).
     */
    @CacheEvict(value = {"events", "eventDetails", "leaderboards"}, allEntries = true)
    public void evictAllCaches() {
        // intentionally empty — annotation handles eviction
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
