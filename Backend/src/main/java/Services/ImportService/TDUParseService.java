package Services.ImportService;

import Models.*;
import Services.Contracts.*;
import Services.DTO.PlayerMatchStats;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class TDUParseService {
    public static Event parseEventFile(String filePath, IPlayerRepository playerRepository, IEventRepository eventRepository) throws Exception{
        File xmlFile = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        // Extract tournament name and ID
        String EventName = getElementText(doc, "name");
        String EventID = getElementText(doc, "id");

        // Determine tournament type from mode
        String EventType = determineTournamentType(doc);

        // Parse players first
        Map<String, Player> playerMap  = parsePlayers(doc, playerRepository);

        //Temporary parse to place uploader id TODO: Grab uploaderID from incoming API call
        String popIdStr = getElementAttribute(doc.getDocumentElement(), "organizer", "popid");
        int uploaderId = popIdStr.isEmpty() ? 0 : Integer.parseInt(popIdStr);

        // Calculate match statistics per player from rounds
        Map<String, PlayerMatchStats> playerStats = calculatePlayerStats(doc);

        // Parse divisions and standings
        List<Tournament> divisions = parseDivisions(doc, playerMap, playerStats, EventType);

        return new Event(EventID, EventName, uploaderId, divisions);
    }

    private static String determineTournamentType(Document doc) {
        String mode = doc.getDocumentElement().getAttribute("mode");
        if (mode.contains("TCG1DAY")) return "Cup";
        if (mode.contains("CHALLENGE")) return "Challenge";
        return "Casual";
    }

    private static String getElementText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }

    private static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }

    private static String getElementAttribute(Element parent, String tagName, String attributeName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodes.item(0);
            return element.getAttribute(attributeName);
        }
        return "";
    }

    private static Map<String, Player> parsePlayers(Document doc, IPlayerRepository playerRepository) {
        Map<String, Player> playerMap = new HashMap<>();
        NodeList playerNodes = doc.getElementsByTagName("player");

        for (int i = 0; i < playerNodes.getLength(); i++) {
            Node node = playerNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            if (!node.getParentNode().getNodeName().equals("players")) continue;

            Element playerElement = (Element) node;
            String userId = playerElement.getAttribute("userid");
            if (userId.isEmpty()) continue;

            String firstName = getElementText(playerElement, "firstname");
            String lastName = getElementText(playerElement, "lastname");
            String fullName = firstName + " " + lastName;

            int playerId = Integer.parseInt(userId);
            Player player = playerRepository.getOrCreatePlayer(playerId, fullName);
            playerMap.put(userId, player);
        }

        return playerMap;
    }

    private static Map<String, PlayerMatchStats> calculatePlayerStats(Document doc) {
        Map<String, PlayerMatchStats> statsMap = new HashMap<>();
        Map<String, Integer> roundsByPlayer = new HashMap<>();

        // 1. Identify Swiss Rounds and Max Round Number
        NodeList allRoundNodes = doc.getElementsByTagName("round");
        List<Element> swissRounds = new ArrayList<>();
        int maxSwissRound = 0;
        for (int i = 0; i < allRoundNodes.getLength(); i++) {
            Element roundElement = (Element) allRoundNodes.item(i);
            String type = roundElement.getAttribute("type");
            // Swiss is type 2 (Challenge) or 3 (Cup)
            if ("2".equals(type) || "3".equals(type)) {
                swissRounds.add(roundElement);
                String numStr = roundElement.getAttribute("number");
                if (!numStr.isEmpty()) {
                    maxSwissRound = Math.max(maxSwissRound, Integer.parseInt(numStr));
                }
            }
        }
        int totalRounds = maxSwissRound;

        // 2. Process Swiss Rounds
        for (Element roundElement : swissRounds) {
            NodeList matchNodes = roundElement.getElementsByTagName("match");
            for (int j = 0; j < matchNodes.getLength(); j++) {
                Element matchElement = (Element) matchNodes.item(j);
                String outcome = matchElement.getAttribute("outcome");

                if (outcome.equals("5")) { // Bye
                    String playerId = getElementAttribute(matchElement, "player", "userid");
                    if (!playerId.isEmpty()) {
                        PlayerMatchStats stats = statsMap.computeIfAbsent(playerId, k -> new PlayerMatchStats());
                        stats.addMatchPoints(3);
                        stats.addBye();
                        roundsByPlayer.merge(playerId, 1, Integer::sum);
                    }
                } else {
                    String p1 = getElementAttribute(matchElement, "player1", "userid");
                    String p2 = getElementAttribute(matchElement, "player2", "userid");
                    if (!p1.isEmpty() && !p2.isEmpty()) {
                        PlayerMatchStats s1 = statsMap.computeIfAbsent(p1, k -> new PlayerMatchStats());
                        PlayerMatchStats s2 = statsMap.computeIfAbsent(p2, k -> new PlayerMatchStats());
                        s1.addMatch(); s2.addMatch();
                        roundsByPlayer.merge(p1, 1, Integer::sum);
                        roundsByPlayer.merge(p2, 1, Integer::sum);
                        switch (outcome) {
                            case "1" -> {
                                s1.addMatchPoints(3);
                                s1.addWin();
                                s2.addMatchPoints(0);
                            }
                            case "2" -> {
                                s2.addMatchPoints(3);
                                s2.addWin();
                                s1.addMatchPoints(0);
                            }
                            case "3" -> {
                                s1.addMatchPoints(1);
                                s2.addMatchPoints(1);
                            }
                        }

                        s1.addOpponent(p2); s2.addOpponent(p1);
                    }
                }
            }
        }
        // Set total tournament rounds for all players
        for (PlayerMatchStats stats : statsMap.values()) {
            stats.setTotalTournamentRounds(totalRounds);
        }

        // 3. Drop Detection (Bounded to Swiss)
        NodeList playerNodes = doc.getElementsByTagName("player");
        for (int i = 0; i < playerNodes.getLength(); i++) {
            Element playerEl = (Element) playerNodes.item(i);
            String userId = playerEl.getAttribute("userid");
            NodeList droppedNodes = playerEl.getElementsByTagName("dropped");
            if (droppedNodes.getLength() > 0) {
                Element droppedEl = (Element) droppedNodes.item(0);
                if ("1".equals(getElementText(droppedEl, "status"))) {
                    String drStr = getElementText(droppedEl, "round");
                    int droppedRound = drStr.isEmpty() ? 0 : Integer.parseInt(drStr);
                    // Only mark as dropped if they left BEFORE finishing Swiss
                    if (totalRounds > 0 && droppedRound <= totalRounds) {
                        statsMap.computeIfAbsent(userId, k -> new PlayerMatchStats()).setDropped(true);
                    }
                }
            }
        }

        // 4. Fallback Inference
        for (Map.Entry<String, Integer> e : roundsByPlayer.entrySet()) {
            if (e.getValue() < totalRounds) {
                PlayerMatchStats s = statsMap.get(e.getKey());
                if (s != null) s.setDropped(true);
            }
        }

        // 5. Finalize Round Counts for All Players
        for (PlayerMatchStats stats : statsMap.values()) {
            stats.setTotalTournamentRounds(totalRounds);
        }

    // 6. Calculate OWP and OOWP with 25% Floor
        for (PlayerMatchStats stats : statsMap.values()) {
            double totalOppWinPct = 0;
            int count = 0;
            for (String oppId : stats.getOpponents()) {
                PlayerMatchStats opp = statsMap.get(oppId);
                if (opp != null) {
                    totalOppWinPct += opp.getWinPercentage();
                    count++;
                }
            }
            stats.setOpponentWinPercentage(count > 0 ? Math.max(0.25, totalOppWinPct / count) : 0.25);
        }

        // 7. Calculate OOWP (Average of OWPs)
        for (PlayerMatchStats stats : statsMap.values()) {
            double totalOWP = 0;
            int count = 0;
            for (String oppId : stats.getOpponents()) {
                PlayerMatchStats opp = statsMap.get(oppId);
                if (opp != null) {
                    totalOWP += opp.getOpponentWinPercentage();
                    count++;
                }
            }
            stats.setOpponentOpponentWinPercentage(count > 0 ? Math.max(0.25, totalOWP / count) : 0.25);
        }

        return statsMap;
    }

    private static List<Tournament> parseDivisions(Document doc, Map<String, Player> playerMap,
                                                           Map<String, PlayerMatchStats> playerStats,
                                                           String tournamentType) {
        List<Tournament> divisions = new ArrayList<>();
        NodeList standingsNodes = doc.getElementsByTagName("standings");

        if (standingsNodes.getLength() == 0) {
            System.out.println("No standings found");
            return divisions;
        }

        Element standingsElement = (Element) standingsNodes.item(0);
        NodeList podNodes = standingsElement.getElementsByTagName("pod");

        for (int i = 0; i < podNodes.getLength(); i++) {
            Element pod = (Element) podNodes.item(i);
            String type = pod.getAttribute("type");
            String category = pod.getAttribute("category");

            // Only process "finished" pods (not "dnf" - did not finish)
            if (!type.equals("finished")) continue;

            // Check if there are any players in this pod
            NodeList playerNodes = pod.getElementsByTagName("player");
            if (playerNodes.getLength() == 0) continue;

            AgeDivision ageDivision = getAgeDivisionFromCategory(category);
            List<Result> results = parseStandings(pod, playerMap, playerStats);

            if (!results.isEmpty()) {
                divisions.add(new Tournament(ageDivision, tournamentType.toLowerCase(), results));
                System.out.println("  - " + ageDivision + " division: " + results.size() + " players");
            }
        }

        return divisions;
    }

    private static AgeDivision getAgeDivisionFromCategory(String category) {
        return switch (category) {
            case "0" -> AgeDivision.Junior;
            case "1" -> AgeDivision.Senior;
            case "2" -> AgeDivision.Master;
            default -> AgeDivision.Master;
        };
    }

    private static List<Result> parseStandings(Element pod, Map<String, Player> playerMap,
                                               Map<String, PlayerMatchStats> playerStats) {
        List<Result> results = new ArrayList<>();
        NodeList playerNodes = pod.getElementsByTagName("player");

        for (int i = 0; i < playerNodes.getLength(); i++) {
            Element playerElement = (Element) playerNodes.item(i);
            String playerId = playerElement.getAttribute("id");
            String placeStr = playerElement.getAttribute("place");

            if (playerId.isEmpty() || placeStr.isEmpty()) continue;

            int placement = Integer.parseInt(placeStr);
            Player player = playerMap.get(playerId);

            if (player != null) {
                PlayerMatchStats stats = playerStats.get(playerId);
                int matchPoints = 0;
                double opponentWinPct = 0.0;
                double opponentOpponentWinPct = 0.0;

                if (stats != null) {
                    matchPoints = stats.getMatchPoints();
                    opponentWinPct = stats.getOpponentWinPercentage();
                    opponentOpponentWinPct = stats.getOpponentOpponentWinPercentage();
                }

                results.add(new Result(player, placement, matchPoints, opponentWinPct, opponentOpponentWinPct));
            }
        }

        return results;
    }
}
