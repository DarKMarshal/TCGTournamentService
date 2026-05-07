package com.darkmarshal.tournamentservice.Services;

import com.darkmarshal.tournamentservice.Models.Event;
import com.darkmarshal.tournamentservice.Contracts.IEventRepository;
import com.darkmarshal.tournamentservice.Contracts.IParseService;
import com.darkmarshal.tournamentservice.DTO.Event.EventSummaryDTO;
import com.darkmarshal.tournamentservice.DTO.Upload.UploadProgressDTO;
import com.darkmarshal.tournamentservice.Services.Broadcast.WebSocketBroadcastService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ImportService {

    private final IEventRepository eventRepository;
    private final IParseService parseService;
    private final CachedDataService cachedDataService;
    private final WebSocketBroadcastService broadcastService;

    public ImportService(IEventRepository eventRepository,
                         IParseService parseService,
                         CachedDataService cachedDataService,
                         WebSocketBroadcastService broadcastService) {
        this.eventRepository = eventRepository;
        this.parseService = parseService;
        this.cachedDataService = cachedDataService;
        this.broadcastService = broadcastService;
    }

    // TODO: This may have to be changed to allow for multiple import types
    public void retrieveEventInformation(String filepath) {
        Event parsedEvent = null;
        try {
            parsedEvent = parseService.parseEventFile(filepath);
        } catch (Exception e) {
            System.out.println("Error parsing event file: " + e.getMessage());
        }
        if (parsedEvent != null) {
            //TODO: Display event information to the user and ask for confirmation before proceeding with import
            eventRepository.saveEvent(parsedEvent);
        }
    }

    /**
     * Processes a .tdf file upload asynchronously on the upload thread pool.
     * Sends progress updates via WebSocket and broadcasts the updated event list on completion.
     *
     * @param jobId        unique identifier for this upload job
     * @param fileName     original file name (for progress messages)
     * @param tempFilePath path to the temporary file on disk
     */
    @Async("uploadExecutor")
    public void processUploadAsync(String jobId, String fileName, String tempFilePath) {
        try {
            // Notify: processing started
            broadcastService.broadcast("/topic/upload/progress",
                    new UploadProgressDTO(jobId, fileName, "PROCESSING", "Import started", 0));

            // Parse the .tdf file
            broadcastService.broadcast("/topic/upload/progress",
                    new UploadProgressDTO(jobId, fileName, "PARSING", "Parsing event file", 0));
            retrieveEventInformation(tempFilePath);

            // Evict stale caches
            broadcastService.broadcast("/topic/upload/progress",
                    new UploadProgressDTO(jobId, fileName, "SAVING", "Updating caches", 0));
            cachedDataService.evictAllCaches();

            // Broadcast updated event list to all WebSocket subscribers
            List<EventSummaryDTO> updatedEvents = cachedDataService.getAllEventSummaries();
            broadcastService.broadcast("/topic/events", updatedEvents);

            // Notify: complete
            broadcastService.broadcast("/topic/upload/progress",
                    new UploadProgressDTO(jobId, fileName, "COMPLETE", "Event imported successfully", 0));

        } catch (Exception e) {
            e.printStackTrace();
            broadcastService.broadcast("/topic/upload/progress",
                    new UploadProgressDTO(jobId, fileName, "FAILED", "Import failed: " + e.getMessage(), 0));
        } finally {
            try {
                Files.deleteIfExists(Path.of(tempFilePath));
            } catch (IOException ignored) {}
        }
    }
}
