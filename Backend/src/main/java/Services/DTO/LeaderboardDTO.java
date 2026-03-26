package Services.DTO;

import java.util.List;
import Models.Player;

public record LeaderboardDTO(String ageDivision, List<PlayerDTO> players) {
}
