package org.example.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, LocalDateTime timestamp, int status, String path) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}