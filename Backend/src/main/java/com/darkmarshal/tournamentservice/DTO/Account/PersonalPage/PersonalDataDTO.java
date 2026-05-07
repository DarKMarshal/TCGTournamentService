package com.darkmarshal.tournamentservice.DTO.Account.PersonalPage;

import java.util.List;

public record PersonalDataDTO(
        PersonalPlayerDTO player,
        List<PersonalResultDTO> results,
        List<PersonalEventDTO> uploadedEvents
) {
}
