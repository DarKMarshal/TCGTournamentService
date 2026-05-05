package com.darkmarshal.tournamentservice.Controllers;

import com.darkmarshal.tournamentservice.DTO.Upload.UploadJob;
import com.darkmarshal.tournamentservice.DTO.Upload.UploadResponseDTO;
import com.darkmarshal.tournamentservice.Services.UploadProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint for .tdf file upload.
 * Accepts one or more files, saves each to a temp location, enqueues them for
 * asynchronous processing, and immediately returns 202 Accepted.
 * Progress and completion are reported per-file via WebSocket on /user/queue/upload-progress.
 */
@RestController
public class FileUploadController {

    private final UploadProcessingService uploadProcessingService;

    @Autowired
    public FileUploadController(UploadProcessingService uploadProcessingService) {
        this.uploadProcessingService = uploadProcessingService;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadTdfFiles(@RequestParam("file") List<MultipartFile> files,
                                            Principal principal) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided");
        }

        String username = principal != null ? principal.getName() : "anonymous";

        List<String> acceptedJobIds = new ArrayList<>();
        List<String> rejectedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                rejectedFiles.add(file.getOriginalFilename() + " (empty file)");
                continue;
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.toLowerCase().endsWith(".tdf")) {
                rejectedFiles.add(originalName + " (not a .tdf file)");
                continue;
            }

            try {
                Path tempFile = Files.createTempFile("tdf-upload-", ".tdf");
                file.transferTo(tempFile.toFile());

                String jobId = UUID.randomUUID().toString();
                UploadJob job = new UploadJob(jobId, originalName, tempFile.toString(), username, 0);

                boolean accepted = uploadProcessingService.enqueue(job);
                if (accepted) {
                    acceptedJobIds.add(jobId);
                } else {
                    rejectedFiles.add(originalName + " (queue full)");
                    Files.deleteIfExists(tempFile);
                }
            } catch (Exception e) {
                rejectedFiles.add(originalName + " (error: " + e.getMessage() + ")");
            }
        }

        // Kick off processing for all enqueued jobs
        for (int i = 0; i < acceptedJobIds.size(); i++) {
            uploadProcessingService.processNextJob();
        }

        if (acceptedJobIds.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    "No files accepted. Rejected: " + rejectedFiles);
        }

        String message = acceptedJobIds.size() + " file(s) accepted for processing.";
        if (!rejectedFiles.isEmpty()) {
            message += " Rejected: " + rejectedFiles;
        }
        message += " Track progress on /user/queue/upload-progress";

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new UploadResponseDTO(
                        acceptedJobIds,
                        uploadProcessingService.getQueueDepth(),
                        message
                ));
    }
}
