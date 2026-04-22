package Services.Network;

import Models.AgeDivision;
import Services.Contracts.IPlayerRepository;
import Services.DTO.Leaderboard.LeaderboardDTO;
import Services.PlayerService.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final IPlayerRepository playerRepository;

    @Autowired
    public LeaderboardController(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> getAllLeaderboards() {
        return ResponseEntity.ok(playerRepository.getLeaderboards());
    }

    @GetMapping("/{ageDivision}")
    public ResponseEntity<LeaderboardDTO> getLeaderboardByAgeDivision(@PathVariable String ageDivision) {
        try {
            AgeDivision division = AgeDivision.valueOf(ageDivision);
            return ResponseEntity.ok(playerRepository.getLeaderboardByAgeDivision(division));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
