package com.example.docuflow_be.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id; // MongoDB uses String for default _id

    private String userId;
    private String documentId;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public NotificationDocument() {
        // Default constructor for MongoDB
    }

    public NotificationDocument(String userId, String documentId, String message, LocalDateTime timestamp) {
        this.userId = userId;
        this.documentId = documentId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
        this.id = java.util.UUID.randomUUID().toString(); // Generate a unique String ID
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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