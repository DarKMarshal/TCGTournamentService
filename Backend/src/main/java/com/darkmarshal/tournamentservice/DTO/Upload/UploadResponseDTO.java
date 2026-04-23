package com.darkmarshal.tournamentservice.DTO.Upload;

import java.util.List;

public record UploadResponseDTO(
        List<String> jobIds,
        int queueDepth,
        String message
) {}