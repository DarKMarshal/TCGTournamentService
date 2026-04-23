package com.darkmarshal.tournamentservice.Controllers;

import com.darkmarshal.tournamentservice.Models.AgeDivision;
import com.darkmarshal.tournamentservice.DTO.Leaderboard.LeaderboardDTO;
import com.darkmarshal.tournamentservice.Services.CachedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final CachedDataService cachedDataService;

    @Autowired
    public LeaderboardController(CachedDataService cachedDataService) {
        this.cachedDataService = cachedDataService;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> getAllLeaderboards() {
        return ResponseEntity.ok(cachedDataService.getAllLeaderboards());
    }

    @GetMapping("/{ageDivision}")
    public ResponseEntity<LeaderboardDTO> getLeaderboardByAgeDivision(@PathVariable String ageDivision) {
        try {
            AgeDivision division = AgeDivision.valueOf(ageDivision);
            return ResponseEntity.ok(cachedDataService.getLeaderboardByAgeDivision(division));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
