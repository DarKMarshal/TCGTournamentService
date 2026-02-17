package Services.Calculation;

import Models.Result;
import Models.Tournament;
import Services.Contracts.IChampionshipPointCalculator;

import java.util.List;

public class CupPointCalculator implements IChampionshipPointCalculator {

    public void calculateChampionshipPoints(Tournament tournament) {
        List<Result> results = tournament.getResults();
        int playerCount = results.size();

        for (Result result : results) {
            int placement = result.getPlacement();
            int points = calculatePoints(placement, playerCount);
            result.setChampionshipPointsEarned(points);
        }
    }

    private int calculatePoints(int placement, int playerCount) {
        int points;

        if (placement == 1) {
            points = 50;
        } else if (placement == 2 && playerCount >=4) {
            points = 40;
        } else if ((placement == 3 || placement == 4) && playerCount >= 8) {
            points = 32;
        } else if ((placement >= 5 && placement <= 8) && playerCount >= 17) {
            points = 25;
        } else if ((placement >= 9 && placement <= 16) && playerCount >= 48) {
            points = 20;
        } else if ((placement >= 17 && placement <= 32) && playerCount >= 80) {
            points = 16;
        } else if ((placement >= 33 && placement <= 64) && playerCount >= 128) {
            points = 13;
        }else
            points = 0;

        return points;
    }
}
