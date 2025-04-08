package com.example.docuflow_be.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReadDocumentResponse {

    private UUID id;
    private String content;
    private String title;
    private String documentType;
    private String submitterUid;
    private LocalDateTime submissionTime;
    private String status;

    public ReadDocumentResponse() {
        // Default constructor
    }

    public ReadDocumentResponse(UUID id, String content, String title, String documentType, String submitterUid, LocalDateTime submissionTime, String status) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}