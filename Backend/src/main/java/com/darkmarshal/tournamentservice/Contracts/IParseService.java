package com.darkmarshal.tournamentservice.Contracts;

import com.darkmarshal.tournamentservice.Models.Event;

public interface IParseService {
    Event parseEventFile(String filePath) throws Exception;
}
