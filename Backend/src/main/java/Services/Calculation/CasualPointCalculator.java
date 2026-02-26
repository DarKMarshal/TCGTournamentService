package Services.Calculation;

import Models.Result;
import Models.Tournament;
import Services.Contracts.IChampionshipPointCalculator;
import org.springframework.lang.NonNull;

import java.util.List;

public class CasualPointCalculator implements IChampionshipPointCalculator {
    public void calculateChampionshipPoints(@NonNull Tournament tournament) {
        List<Result> results = tournament.getResults();

        for (Result result : results) {
            result.setChampionshipPointsEarned(0);
        }
    }
}
