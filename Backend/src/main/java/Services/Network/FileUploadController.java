package Services.Network;

import Database.Repositories.EventRepository;
import Services.DTO.EventSummaryDTO;
import Services.ImportService.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

/**
 * REST endpoint for .tdf file upload.
 * After a successful import, broadcasts an updated event list via WebSocket.
 */
@RestController
@CrossOrigin(originPatterns = "*")
public class FileUploadController {

    private final Connection connection;
    private final EventRepository eventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FileUploadController(
            Connection connection,
            EventRepository eventRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.connection = connection;
        this.eventRepository = eventRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<String> uploadTdfFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".tdf")) {
            return ResponseEntity.badRequest().body("Only .tdf files are accepted");
        }

        Path tempFile = null;
        try {
            // Save uploaded file to a temp location
            tempFile = Files.createTempFile("tdf-upload-", ".tdf");
            file.transferTo(tempFile.toFile());

            // Run the existing import pipeline
            ImportService.retrieveEventInformation(connection, tempFile.toString());

            // Broadcast updated event list to all WebSocket subscribers
            List<EventSummaryDTO> updatedEvents = eventRepository.getAllEvents().stream()
                    .map(e -> new EventSummaryDTO(e.getId(), e.getName()))
                    .toList();
            messagingTemplate.convertAndSend("/topic/events", updatedEvents);

            return ResponseEntity.ok("Event imported successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Import failed: " + e.getMessage());
        } finally {
            // Clean up temp file
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }
}
