package Services.Network;

import Models.AgeDivision;
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

    private final Connection connection;

    @Autowired
    public LeaderboardController(Connection connection) {
        this.connection = connection;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> getAllLeaderboards(){
        return ResponseEntity.ok(PlayerService.getLeaderboards(connection));
    }

    @GetMapping("/{ageDivision}")
    public ResponseEntity<LeaderboardDTO> getLeaderboardByAgeDivision(@PathVariable String ageDivision){
        try {
            AgeDivision division = AgeDivision.valueOf(ageDivision);
            return ResponseEntity.ok(PlayerService.getLeaderboardByDivision(connection, division));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
