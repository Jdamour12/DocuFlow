package com.example.docuflow_backend.dto;

import com.example.docuflow_backend.model.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String fileName;
    
    private String contentType;
    
    private Long fileSize;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
    
    private String lastModifiedBy;
    
    private LocalDateTime lastModifiedAt;
    
    private DocumentStatus status;
    
    private Long version;
}
