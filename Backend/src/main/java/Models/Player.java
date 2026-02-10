package Models;

public class Player {
    private int id;
    private String name;
    private int championshipPoints;

    public Player(int id, String name, int championshipPoints) {
        this.id = id;
        this.name = name;
        this.championshipPoints = championshipPoints;
    }

    public Player(int id, String name) {
        this(id, name, 0);
    }

    //Getters and Setters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getChampionshipPoints() {
        return championshipPoints;
    }
    public void setChampionshipPoints(int championshipPoints) {
        this.championshipPoints = championshipPoints;
    }

}
