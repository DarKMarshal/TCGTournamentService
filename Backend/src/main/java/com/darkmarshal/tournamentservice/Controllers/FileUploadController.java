package com.darkmarshal.tournamentservice.Controllers;

import com.darkmarshal.tournamentservice.DTO.Upload.UploadResponseDTO;
import com.darkmarshal.tournamentservice.Services.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint for .tdf file upload.
 * Accepts the file, saves it to a temp location, and immediately returns 202 Accepted.
 * The actual import pipeline runs asynchronously on the upload thread pool.
 * Progress and completion are reported to clients via WebSocket on /topic/upload/progress.
 */
@RestController
public class FileUploadController {

    private final ImportService importService;

    @Autowired
    public FileUploadController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadTdfFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".tdf")) {
            return ResponseEntity.badRequest().body("Only .tdf files are accepted");
        }

        try {
            // Save uploaded file to a temp location
            Path tempFile = Files.createTempFile("tdf-upload-", ".tdf");
            file.transferTo(tempFile.toFile());

            // Generate a job ID so the client can track progress via WebSocket
            String jobId = UUID.randomUUID().toString();

            // Kick off async processing — returns immediately
            importService.processUploadAsync(jobId, originalName, tempFile.toString());

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new UploadResponseDTO(
                            List.of(jobId),
                            1,
                            "Upload accepted. Track progress on /topic/upload/progress"
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to accept upload: " + e.getMessage());
        }
    }
}
