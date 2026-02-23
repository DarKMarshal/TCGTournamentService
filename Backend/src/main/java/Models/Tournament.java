package Models;

import Services.Calculation.*;
import Services.Contracts.IChampionshipPointCalculator;
import org.springframework.lang.NonNull;

import java.util.List;

public class Tournament {
    private final AgeDivision ageDivision;
    private final String tournamentType;
    private final IChampionshipPointCalculator championshipPointCalculator;
    private final List<Result> results;

    public Tournament(AgeDivision ageDivision, String tournamentType, List<Result> results) {
        this.ageDivision = ageDivision;
        this.tournamentType = tournamentType;
        this.championshipPointCalculator = createPointCalculator(tournamentType);
        this.results = results;
        calculateChampionshipPoints();
    }

    @NonNull
    private IChampionshipPointCalculator createPointCalculator(String tournamentType) {
        return switch (tournamentType) {
            case "casual" -> new CasualPointCalculator();
            case "challenge" -> new ChallengePointCalculator();
            case "cup" -> new CupPointCalculator();
            default -> throw new IllegalArgumentException("Unknown tournament type: " + tournamentType);
        };
    }

    public List<Result> getResults() {
        return results;
    }

    public AgeDivision getAgeDivision() {
        return ageDivision;
    }

    public String getTournamentType() {
        return tournamentType;
    }
    public void calculateChampionshipPoints(){
        championshipPointCalculator.calculateChampionshipPoints(this);
    }
}
