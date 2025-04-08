package com.example.docuflow_be.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "document_history")
public class DocumentHistoryDocument {

    @Id
    private String id; // MongoDB uses String for default _id

    private UUID documentId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    public DocumentHistoryDocument() {
        // Default constructor for MongoDB
    }

    public DocumentHistoryDocument(UUID documentId, String action, String performedBy, LocalDateTime timestamp) {
        this.documentId = documentId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.id = UUID.randomUUID().toString(); // Generate a unique String ID
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}