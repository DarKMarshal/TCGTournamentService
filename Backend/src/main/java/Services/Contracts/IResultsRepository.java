package Services.Contracts;

import Models.Result;
import Services.DTO.Account.PersonalPage.PersonalResultDTO;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IResultsRepository {
    void saveResults(String eventId, String ageDivision, @NonNull List<Result> results) throws SQLException;

    void saveResults(Connection conn, String eventId, String ageDivision, List<Result> results) throws SQLException;

    List<Result> getResultsByEventAndDivision(String eventId, String ageDivision);

    List<PersonalResultDTO> findResultsByPlayerId(int playerId);
}
