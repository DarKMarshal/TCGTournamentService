package Services.Network;

import Services.Contracts.IEventRepository;
import Services.Contracts.IPlayerRepository;
import Services.Contracts.IResultsRepository;
import Services.DTO.Account.PersonalPage.PersonalDataDTO;
import Services.DTO.Account.PersonalPage.PersonalEventDTO;
import Services.DTO.Account.PersonalPage.PersonalPlayerDTO;
import Services.DTO.Account.PersonalPage.PersonalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    private final IPlayerRepository playerRepository;
    private final IResultsRepository resultsRepository;
    private final IEventRepository eventRepository;

    @Autowired
    public PersonalController(IPlayerRepository playerRepository, IResultsRepository resultsRepository, IEventRepository eventRepository) {
        this.playerRepository  = playerRepository;
        this.resultsRepository = resultsRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPersonalData(@PathVariable int playerId) {
            // 1. Look up the player
            PersonalPlayerDTO player = playerRepository.findPersonalPlayer(playerId);

            // 2. Get results for this player (empty list if player not found)
            List<PersonalResultDTO> results = player != null
                    ? resultsRepository.findResultsByPlayerId(playerId)
                    : List.of();

            // 3. Get events uploaded by this player
            List<PersonalEventDTO> uploadedEvents = eventRepository.findEventsByUploaderId(playerId);

            return ResponseEntity.ok(new PersonalDataDTO(player, results, uploadedEvents));
    }
}
