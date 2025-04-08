package com.example.docuflow_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitDocumentRequest {

    @NotBlank(message = "Document content cannot be blank")
    @Size(min = 1, max = 10000, message = "Document content must be between 1 and 10000 characters")
    private String content;

    @NotBlank(message = "Document title cannot be blank")
    @Size(min = 1, max = 255, message = "Document title must be between 1 and 255 characters")
    private String title;

    private String documentType; // Can be blank

    public SubmitDocumentRequest() {
        // Default constructor
    }

    public SubmitDocumentRequest(String content, String title, String documentType) {
        this.content = content;
        this.title = title;
        this.documentType = documentType;
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
}