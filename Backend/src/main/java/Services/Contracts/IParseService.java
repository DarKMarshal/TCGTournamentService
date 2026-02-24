package Services.Contracts;

import Models.*;
import Services.DTO.PlayerMatchStats;

import java.util.List;
import java.util.Map;

public abstract class IParseService {
     public static Event parseEventFile;
     private static String determineTournamentType;
     private static Map<String, Player> parsePlayers;
     private static Map<String, PlayerMatchStats> calculatePlayerStats;
     private static List<Tournament> parseDivisions;
     private static List<Result> parseStandings;
     private static AgeDivision getAgeDivisionFromCategory;

}
