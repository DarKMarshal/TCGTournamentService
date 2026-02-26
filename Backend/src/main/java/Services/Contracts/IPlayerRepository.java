package Services.Contracts;

import Models.Player;
import Models.Result;
import org.springframework.lang.NonNull;

import java.sql.SQLException;
import java.util.List;

public interface IPlayerRepository {
    void savePlayer(@NonNull Player player);

    Player getPlayerById(int id);

    Player getOrCreatePlayer(int id, String name);

    List<Player> getAllPlayers();

    void updatePlayerChampionshipPoints(@NonNull List<Result> results) throws SQLException;

    void deletePlayer(int id);
}
