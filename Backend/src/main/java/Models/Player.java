package Models;

public class Player {
    private int id;
    private String name;
    private int championshipPoints;
    private AgeDivision ageDivision;

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
    public AgeDivision getAgeDivision() {
        return ageDivision;
    }
    public void setAgeDivision(AgeDivision ageDivision) {
        this.ageDivision = ageDivision;
    }

    // This shouldn't be needed, but it's here just in case
    public void setChampionshipPoints(int championshipPoints) {
        this.championshipPoints = championshipPoints;
    }

}
