package com.darkmarshal.tournamentservice.DTO.Upload;

public record UploadProgressDTO(
        String jobId,
        String fileName,
        String status,     // QUEUED, PROCESSING, PARSING, SAVING, COMPLETE, FAILED
        String message,
        int queuePosition
) {}
