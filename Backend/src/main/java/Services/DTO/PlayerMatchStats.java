package Services.DTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerMatchStats {
    private int matchPoints = 0;
    private int matchesPlayed = 0;
    private int wins = 0;
    private int byes = 0;
    private int roundsParticipated = 0;
    private boolean isDropped = false;
    private int totalTournamentRounds = 0;
    private List<String> opponents = new ArrayList<>();
    private double opponentWinPercentage = 0.0;
    private double opponentOpponentWinPercentage = 0.0;


    public void addMatchPoints(int points) {
        this.matchPoints += points;
    }

    public void addMatch() {
        this.matchesPlayed++;
        this.roundsParticipated++;
    }

    public void addWin() {
        this.wins++;
    }

    public void addBye() {
        this.byes++;
        this.roundsParticipated++;
    }

    public void setDropped(boolean dropped) {
        isDropped = dropped;
    }

    public void setTotalTournamentRounds(int totalTournamentRounds) {
        this.totalTournamentRounds = totalTournamentRounds;
    }

    public void addOpponent(String opponentId) {
        this.opponents.add(opponentId);
    }

    public boolean isDropped() { return isDropped; }
    public int getRoundsParticipated() { return roundsParticipated; }
    public int getTotalTournamentRounds() { return totalTournamentRounds; }

    public int getByes() { return byes; }

    public int getMatchPoints() {
        return matchPoints;
    }

    public double getWinPercentage() {
        // Use the appropriate round count based on drop status
        int rounds = isDropped ? roundsParticipated : totalTournamentRounds;
        // Exclude byes from the denominator
         

        int effectiveMatches = rounds - byes;
        double winPct = 0.0;
        if (effectiveMatches > 0) {
            // Subtract bye points (3 per bye) to get points from actual matches
            winPct = (double) (matchPoints - 3 * byes) / (effectiveMatches * 3);
        }

        double min = 0.25; // 25% Floor
        double max = isDropped ? 0.75 : 1.00;

        return Math.max(min, Math.min(max, winPct));
    }

    public List<String> getOpponents() {
        return opponents;
    }

    public void setOpponentWinPercentage(double pct) {
        this.opponentWinPercentage = pct;
    }

    public double getOpponentWinPercentage() {
        return opponentWinPercentage;
    }

    public void setOpponentOpponentWinPercentage(double pct) {
        this.opponentOpponentWinPercentage = pct;
    }

    public double getOpponentOpponentWinPercentage() {
        return opponentOpponentWinPercentage;
    }
}
