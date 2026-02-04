package Models;

import java.util.List;
import java.util.Map;

public class Tournament {
    private int id;
    private String name;
    private Map<AgeDivision, DivisionData> divisions; // Helper class or structure for divisions

    public Tournament(int id, String name, Map<AgeDivision, DivisionData> divisions) {
        this.id = id;
        this.name = name;
        this.divisions = divisions;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Map<AgeDivision, DivisionData> getDivisions() { return divisions; }

    public static class DivisionData {
        private String tournamentType;
        private List<Result> results;

        public DivisionData(String tournamentType, List<Result> results) {
            this.tournamentType = tournamentType;
            this.results = results;
        }

        public String getTournamentType() { return tournamentType; }
        public List<Result> getResults() { return results; }
    }
}
