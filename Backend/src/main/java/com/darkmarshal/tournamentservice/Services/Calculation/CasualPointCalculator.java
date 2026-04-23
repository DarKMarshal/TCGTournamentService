package com.darkmarshal.tournamentservice.Services.Calculation;

import com.darkmarshal.tournamentservice.Models.Result;
import com.darkmarshal.tournamentservice.Models.Tournament;
import com.darkmarshal.tournamentservice.Contracts.IChampionshipPointCalculator;
import org.springframework.lang.NonNull;

import java.util.List;

public class CasualPointCalculator implements IChampionshipPointCalculator {
    public void calculateChampionshipPoints(@NonNull Tournament tournament) {
        List<Result> results = tournament.getResults();

        for (Result result : results) {
            result.setChampionshipPointsEarned(0);
        }
    }
}
