package Services.PlayerService;

import Database.Repositories.PlayerRepository;
import Models.AgeDivision;
import Models.Player;
import Services.Contracts.IPlayerRepository;
import Services.DTO.Leaderboard.LeaderboardDTO;
import Services.DTO.Account.PersonalPage.PersonalPlayerDTO;
import Services.DTO.Event.PlayerDTO;
import org.jetbrains.annotations.Contract;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PlayerService {

    @NonNull
    public static List<LeaderboardDTO> getLeaderboards(Connection connection){
        List<LeaderboardDTO> leaderboards = new ArrayList<>();
        for (AgeDivision ageDivision : AgeDivision.values()) {
            leaderboards.add(getLeaderboardByDivision(connection, ageDivision));
        }
        return leaderboards;
    }

    @NonNull
    public static LeaderboardDTO getLeaderboardByDivision(Connection connection, AgeDivision ageDivision){
        IPlayerRepository playerRepository = new PlayerRepository(connection);
        List<PlayerDTO> playerDTOs = playerRepository.getPlayersByAgeDivision(ageDivision)
                .stream()
                .map(p -> new PlayerDTO(p.getName(), p.getChampionshipPoints()))
                .toList();
        return new LeaderboardDTO(ageDivision.name(), playerDTOs);
    }

    @NonNull
    @Contract("_, _ -> new")
    public static PersonalPlayerDTO getPlayerById(Connection connection, int id){
        IPlayerRepository playerRepository = new PlayerRepository(connection);
        Player player = playerRepository.getPlayerById(id);
        return new PersonalPlayerDTO(player.getId(), player.getName(), player.getChampionshipPoints());
    }

    public static void manuallySavePlayer(Connection connection, @NonNull PersonalPlayerDTO playerDTO){
        IPlayerRepository playerRepository = new PlayerRepository(connection);
        playerRepository.savePlayer(new Player(playerDTO.id(), playerDTO.name(), playerDTO.championshipPoints()));
    }

}
