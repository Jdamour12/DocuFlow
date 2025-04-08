package com.example.docuflow_backend.repository.jpa;

import com.example.docuflow_backend.model.DocumentMetadata;
import com.example.docuflow_backend.model.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, Long> {
    
    List<DocumentMetadata> findByCreatedBy(String createdBy);
    
    List<DocumentMetadata> findByStatus(DocumentStatus status);
    
    List<DocumentMetadata> findByTitleContainingIgnoreCase(String title);
}
