package com.darkmarshal.tournamentservice.Models;

import java.util.Collections;
import java.util.List;

public class Event {
    private final String id;
    private final String name;
    private final int uploaderID;
    private final List<Tournament> divisions; // Helper class or structure for divisions

    public Event(String id, String name, int uploaderID, List<Tournament> divisions) {
        this.id = id;
        this.name = name;
        this.uploaderID = uploaderID;
        this.divisions = divisions;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getUploaderID() { return uploaderID; }
    public List<Tournament> getDivisions() {
        return Collections.unmodifiableList(divisions);
    }


}
