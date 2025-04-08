package com.example.docuflow_be.entity;

import com.example.docuflow_be.model.DocumentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    private String documentType;

    private String submitterUid;

    private LocalDateTime submissionTime;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    public DocumentEntity() {
        // Default constructor for JPA
    }

    public DocumentEntity(String content, String title, String documentType, String submitterUid, LocalDateTime submissionTime, DocumentStatus status) {
        this.content = content;
        this.title = title;
        this.documentType = documentType;
        this.submitterUid = submitterUid;
        this.submissionTime = submissionTime;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getSubmitterUid() {
        return submitterUid;
    }

    public void setSubmitterUid(String submitterUid) {
        this.submitterUid = submitterUid;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
}