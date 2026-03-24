package Services.DTO;

import Models.Result;

import java.util.List;

/**
 * A single age-division's data within an event.
 */
public record DivisionDTO(String ageDivision, String tournamentType, List<Result> results) {}
