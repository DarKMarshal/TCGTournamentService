package com.darkmarshal.tournamentservice.DTO.Account.PersonalPage;

public record PersonalResultDTO(
        String eventId,
        String eventName,
        String ageDivision,
        int placement,
        int pointsEarned
) {
}
