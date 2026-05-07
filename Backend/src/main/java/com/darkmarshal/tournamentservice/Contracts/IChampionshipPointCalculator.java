package com.darkmarshal.tournamentservice.Contracts;

import com.darkmarshal.tournamentservice.Models.Tournament;

public interface IChampionshipPointCalculator {
    public void calculateChampionshipPoints(Tournament tournament);

}
