package Services.DTO.Leaderboard;

import java.util.List;

import Services.DTO.Event.PlayerDTO;

public record LeaderboardDTO(String ageDivision, List<PlayerDTO> players) {
}
