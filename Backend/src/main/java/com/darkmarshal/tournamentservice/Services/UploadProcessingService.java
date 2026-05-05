package com.darkmarshal.tournamentservice.Services;

import com.darkmarshal.tournamentservice.Contracts.IEventRepository;
import com.darkmarshal.tournamentservice.Contracts.IParseService;
import com.darkmarshal.tournamentservice.DTO.Event.EventSummaryDTO;
import com.darkmarshal.tournamentservice.DTO.Upload.UploadJob;
import com.darkmarshal.tournamentservice.DTO.Upload.UploadProgressDTO;
import com.darkmarshal.tournamentservice.Models.Event;
import com.darkmarshal.tournamentservice.Services.Broadcast.WebSocketBroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UploadProcessingService {

    private static final int MAX_QUEUE_SIZE = 200;

    private final BlockingQueue<UploadJob> jobQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    private final AtomicInteger activeJobs = new AtomicInteger(0);

    private final IParseService parseService;
    private final IEventRepository eventRepository;
    private final CachedDataService cachedDataService;
    private final WebSocketBroadcastService broadcastService;

    @Autowired
    public UploadProcessingService(IParseService parseService,
                                   IEventRepository eventRepository,
                                   CachedDataService cachedDataService,
                                   WebSocketBroadcastService broadcastService) {
        this.parseService = parseService;
        this.eventRepository = eventRepository;
        this.cachedDataService = cachedDataService;
        this.broadcastService = broadcastService;
    }

    /**
     * Enqueue a job. Returns false if the queue is full (backpressure).
     */
    public boolean enqueue(UploadJob job) {
        job.setQueuePosition(jobQueue.size() + 1);
        boolean accepted = jobQueue.offer(job);
        if (accepted) {
            sendProgress(job, UploadJob.Status.QUEUED,
                    "Queued at position " + job.getQueuePosition());
        }
        return accepted;
    }

    public int getQueueDepth() {
        return jobQueue.size();
    }

    /**
     * Called asynchronously — drains one job from the queue and processes it.
     * Multiple threads can run this concurrently (controlled by the uploadExecutor pool).
     */
    @Async("uploadExecutor")
    public void processNextJob() {
        UploadJob job = jobQueue.poll();
        if (job == null) return;

        activeJobs.incrementAndGet();
        Path tempFile = Path.of(job.getTempFilePath());

        try {
            // Phase 1: Parsing
            sendProgress(job, UploadJob.Status.PARSING, "Parsing file...");

            Event parsedEvent = parseService.parseEventFile(job.getTempFilePath());

            if (parsedEvent == null) {
                sendProgress(job, UploadJob.Status.FAILED, "Failed to parse file — no event data found");
                return;
            }

            // Phase 2: Saving
            sendProgress(job, UploadJob.Status.SAVING,
                    "Saving event: " + parsedEvent.getName());
            eventRepository.saveEvent(parsedEvent);

            // Phase 3: Cache eviction and broadcast
            cachedDataService.evictAllCaches();

            sendProgress(job, UploadJob.Status.COMPLETE,
                    "Successfully imported: " + parsedEvent.getName());

            // Broadcast updated event list to all subscribers
            List<EventSummaryDTO> updatedEvents = cachedDataService.getAllEventSummaries();
            broadcastService.broadcast("/topic/events", updatedEvents);

        } catch (Exception e) {
            sendProgress(job, UploadJob.Status.FAILED, "Import failed: " + e.getMessage());
        } finally {
            activeJobs.decrementAndGet();
            try { Files.deleteIfExists(tempFile); } catch (Exception ignored) {}

            // If more jobs in queue, keep processing
            if (!jobQueue.isEmpty()) {
                processNextJob();
            }
        }
    }

    private void sendProgress(UploadJob job, UploadJob.Status status, String message) {
        job.setStatus(status);
        job.setMessage(message);
        broadcastService.sendToUser(
                job.getUploaderUsername(),
                "/queue/upload-progress",
                new UploadProgressDTO(
                        job.getJobId(),
                        job.getFileName(),
                        status.name(),
                        message,
                        job.getQueuePosition()
                )
        );
    }
}
