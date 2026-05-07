package com.darkmarshal.tournamentservice.DTO.Leaderboard;

import java.util.List;

import com.darkmarshal.tournamentservice.DTO.Event.PlayerDTO;

public record LeaderboardDTO(String ageDivision, List<PlayerDTO> players) {
}
