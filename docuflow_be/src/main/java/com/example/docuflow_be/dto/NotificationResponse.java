package com.example.docuflow_be.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private String id; // Can be Long for JPA or String for MongoDB
    private String documentId;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public NotificationResponse() {
        // Default constructor
    }

    public NotificationResponse(String id, String documentId, String message, LocalDateTime timestamp, boolean isRead) {
        this.id = id;
        this.documentId = documentId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}