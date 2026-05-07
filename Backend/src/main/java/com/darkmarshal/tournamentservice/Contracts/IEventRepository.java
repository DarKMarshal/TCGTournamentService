package com.darkmarshal.tournamentservice.Contracts;

import com.darkmarshal.tournamentservice.Models.Event;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalEventDTO;
import org.springframework.lang.NonNull;

import java.util.List;

public interface IEventRepository {
    void saveEvent(@NonNull Event event);

    void deleteEvent(String id);

    Event getEventById(String id);

    public List<Event> getAllEvents();

    List<PersonalEventDTO> findEventsByUploaderId(int uploaderId);
}
