package com.example.docuflow_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEvent {
    
    private Long documentId;
    
    private String documentTitle;
    
    private DocumentEventType eventType;
    
    private DocumentStatus previousStatus;
    
    private DocumentStatus newStatus;
    
    private String initiatedBy;
    
    private LocalDateTime timestamp;
    
    private String comments;
    
    public enum DocumentEventType {
        CREATED,
        UPDATED,
        STATUS_CHANGED,
        DELETED
    }
}
