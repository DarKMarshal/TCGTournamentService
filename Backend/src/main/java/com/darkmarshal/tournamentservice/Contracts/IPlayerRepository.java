package com.darkmarshal.tournamentservice.Contracts;

import com.darkmarshal.tournamentservice.Models.AgeDivision;
import com.darkmarshal.tournamentservice.Models.Player;
import com.darkmarshal.tournamentservice.Models.Result;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalPlayerDTO;
import com.darkmarshal.tournamentservice.DTO.Leaderboard.LeaderboardDTO;
import org.springframework.lang.NonNull;

import java.sql.SQLException;
import java.util.List;

public interface IPlayerRepository {
    void savePlayer(@NonNull Player player);

    Player getPlayerById(int id);

    Player getOrCreatePlayer(int id, String name);

    List<Player> getAllPlayers();

    List<Player> getPlayersByAgeDivision(AgeDivision ageDivision);

    List<LeaderboardDTO> getLeaderboards();

    LeaderboardDTO getLeaderboardByAgeDivision(AgeDivision ageDivision);

    PersonalPlayerDTO findPersonalPlayer(int id);

    void updatePlayerChampionshipPoints(@NonNull List<Result> results) throws SQLException;

    void updatePlayerAgeDivisions(@NonNull List<Result> results, AgeDivision ageDivision) throws SQLException;

    void deletePlayer(int id);
}
