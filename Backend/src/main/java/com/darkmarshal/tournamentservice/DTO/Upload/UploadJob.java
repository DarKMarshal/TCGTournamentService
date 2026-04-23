package com.darkmarshal.tournamentservice.DTO.Upload;

public class UploadJob {
    public enum Status { QUEUED, PROCESSING, PARSING, SAVING, COMPLETE, FAILED }

    private final String jobId;
    private final String fileName;
    private final String tempFilePath;
    private final String uploaderUsername; // for WebSocket targeting
    private final int uploaderId;
    private Status status;
    private String message;
    private int queuePosition;

    public UploadJob(String jobId, String fileName, String tempFilePath,
                     String uploaderUsername, int uploaderId) {
        this.jobId = jobId;
        this.fileName = fileName;
        this.tempFilePath = tempFilePath;
        this.uploaderUsername = uploaderUsername;
        this.uploaderId = uploaderId;
        this.status = Status.QUEUED;
    }

    // Getters and setters for all fields...

    public String getJobId() { return jobId; }
    public String getFileName() { return fileName; }
    public String getTempFilePath() { return tempFilePath; }
    public String getUploaderUsername() { return uploaderUsername; }
    public int getUploaderId() { return uploaderId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }
}
