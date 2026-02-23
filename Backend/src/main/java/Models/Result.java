package Models;

public class Result {
    private Player player;
    private int placement;
    private int matchPoints;
    private double opponentWinPercentage;
    private double opponentOpponentWinPercentage;
    private int championshipPointsEarned;

    public Result(Player player, int placement, int matchPoints, double opponentWinPercentage, double opponentOpponentWinPercentage) {
        this.player = player;
        this.placement = placement;
        this.matchPoints = matchPoints;
        this.opponentWinPercentage = opponentWinPercentage;
        this.opponentOpponentWinPercentage = opponentOpponentWinPercentage;
    }

    // Getters
    public Player getPlayer() {
        return player;
    }
    public int getPlacement() {
        return placement;
    }
    public int getMatchPoints() {
        return matchPoints;
    }
    public double getOpponentWinPercentage() {
        return opponentWinPercentage;
    }
    public double getOpponentOpponentWinPercentage() {
        return opponentOpponentWinPercentage;
    }
    public int getChampionshipPointsEarned() {
        return championshipPointsEarned;
    }
    public void setChampionshipPointsEarned(int championshipPointsEarned) {
        this.championshipPointsEarned = championshipPointsEarned;
    }
}
