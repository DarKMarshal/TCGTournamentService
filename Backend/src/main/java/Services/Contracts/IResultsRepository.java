package Services.Contracts;

import Models.Result;
import org.springframework.lang.NonNull;

import java.sql.SQLException;
import java.util.List;

public interface IResultsRepository {
    void saveResults(String eventId, String ageDivision, @NonNull List<Result> results) throws SQLException;

    List<Result> getResultsByEventAndDivision(String eventId, String ageDivision);
}
