package com.example.docuflow_be.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentHistoryResponse {
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    public DocumentHistoryResponse() {
        // Default constructor
    }

    public DocumentHistoryResponse(String action, String performedBy, LocalDateTime timestamp) {
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
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