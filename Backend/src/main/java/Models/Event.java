package Models;

import java.util.List;
import java.util.Map;

public class Event {
    private int id;
    private String name;
    private List<Tournament> divisions; // Helper class or structure for divisions

    public Event(int id, String name, List<Tournament> divisions) {
        this.id = id;
        this.name = name;
        this.divisions = divisions;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Tournament> getDivisions() {
        return divisions;
    }


}
