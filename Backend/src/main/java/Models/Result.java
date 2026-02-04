package Models;

public class Result {
    private Player player;
    private int placement;
    private int points;
    private int matchPoints;
    private double opponentWinPercentage;
    private double opponentOpponentWinPercentage;

    public Result(Player player, int placement, int points, int matchPoints, double opponentWinPercentage, double opponentOpponentWinPercentage) {
        this.player = player;
        this.placement = placement;
        this.points = points;
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
    public int getPoints() {
        return points;
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
}
