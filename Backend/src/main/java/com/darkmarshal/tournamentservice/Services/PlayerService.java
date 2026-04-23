package com.darkmarshal.tournamentservice.Services;

import com.darkmarshal.tournamentservice.Models.AgeDivision;
import com.darkmarshal.tournamentservice.Models.Player;
import com.darkmarshal.tournamentservice.Contracts.IPlayerRepository;
import com.darkmarshal.tournamentservice.DTO.Leaderboard.LeaderboardDTO;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalPlayerDTO;
import com.darkmarshal.tournamentservice.DTO.Event.PlayerDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    private final IPlayerRepository playerRepository;

    public PlayerService(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // TODO: This File might not be needed, most of the functionality has been moved to Player Repo.
    @NonNull
    public List<LeaderboardDTO> getLeaderboards() {
        List<LeaderboardDTO> leaderboards = new ArrayList<>();
        for (AgeDivision ageDivision : AgeDivision.values()) {
            leaderboards.add(getLeaderboardByDivision(ageDivision));
        }
        return leaderboards;
    }

    @NonNull
    public LeaderboardDTO getLeaderboardByDivision(AgeDivision ageDivision) {
        List<PlayerDTO> playerDTOs = playerRepository.getPlayersByAgeDivision(ageDivision)
                .stream()
                .map(p -> new PlayerDTO(p.getName(), p.getChampionshipPoints()))
                .toList();
        return new LeaderboardDTO(ageDivision.name(), playerDTOs);
    }

    @NonNull
    public PersonalPlayerDTO getPlayerById(int id) {
        Player player = playerRepository.getPlayerById(id);
        return new PersonalPlayerDTO(player.getId(), player.getName(), player.getChampionshipPoints());
    }

    public void manuallySavePlayer(@NonNull PersonalPlayerDTO playerDTO) {
        playerRepository.savePlayer(new Player(playerDTO.id(), playerDTO.name(), playerDTO.championshipPoints()));
    }
}
