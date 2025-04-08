package com.example.docuflow_backend.service.impl;

import com.example.docuflow_backend.dto.DocumentDTO;
import com.example.docuflow_backend.exception.DocumentNotFoundException;
import com.example.docuflow_backend.messaging.DocumentEventPublisher;
import com.example.docuflow_backend.model.DocumentContent;
import com.example.docuflow_backend.model.DocumentEvent;
import com.example.docuflow_backend.model.DocumentMetadata;
import com.example.docuflow_backend.model.DocumentStatus;
import com.example.docuflow_backend.repository.jpa.DocumentMetadataRepository;
import com.example.docuflow_backend.repository.mongo.DocumentContentRepository;
import com.example.docuflow_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Service
@RequiredArgsConstructor
@Slf4j
@Repository
public class DocumentServiceImpl implements DocumentService {

        private final DocumentMetadataRepository metadataRepository;
        private final DocumentContentRepository contentRepository;
        private final DocumentEventPublisher eventPublisher;

    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String title, String description, String username) {
        try {
            // Save document content to MongoDB
            DocumentContent documentContent = DocumentContent.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .content(file.getBytes())
                .build();
        DocumentContent savedContent = contentRepository.save(documentContent);
        
        // Save metadata to PostgreSQL
        DocumentMetadata metadata = DocumentMetadata.builder()
                .title(title)
                .description(description)
                .fileId(String.valueOf(savedContent.getId()))
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .createdBy(username)
                .createdAt(LocalDateTime.now())
                .status(DocumentStatus.DRAFT)
                .build();
        
        DocumentMetadata savedMetadata = metadataRepository.save(metadata);
        
            // Update the reference in MongoDB
        savedContent.setMetadataId(savedMetadata.getId());
        contentRepository.save(savedContent);
        
            // Publish event
        eventPublisher.publishDocumentEvent(
                DocumentEvent.builder()
                        .documentId(savedMetadata.getId())
                        .documentTitle(savedMetadata.getTitle())
                        .eventType(DocumentEvent.DocumentEventType.CREATED)
                        .newStatus(DocumentStatus.DRAFT)
                        .initiatedBy(username)
                        .timestamp(LocalDateTime.now())
                        .build()
            );
            
            return mapToDTO(savedMetadata);
        } catch (IOException e) {
            log.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDTO getDocument(Long id) {
        DocumentMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        
        return mapToDTO(metadata);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getDocumentContent(Long id) {
        DocumentMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        Long fileIdToDelete = Long.parseLong(metadata.getFileId());
        DocumentContent content = contentRepository.findById(fileIdToDelete)
                .orElseThrow(() -> new DocumentNotFoundException("Document content not found for id: " + id));
        
        return content.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getAllDocuments() {
        return metadataRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByUser(String username) {
        return metadataRepository.findByCreatedBy(username).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByStatus(DocumentStatus status) {
        return metadataRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDTO> searchDocuments(String query) {
        return metadataRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentDTO updateDocumentMetadata(Long id, String title, String description, String username) {
        DocumentMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        
        metadata.setTitle(title);
        metadata.setDescription(description);
        metadata.setLastModifiedBy(username);
        metadata.setLastModifiedAt(LocalDateTime.now());
        
        DocumentMetadata updatedMetadata = metadataRepository.save(metadata);
        
        // Publish event
        eventPublisher.publishDocumentEvent(
                DocumentEvent.builder()
                        .documentId(updatedMetadata.getId())
                        .documentTitle(updatedMetadata.getTitle())
                        .eventType(DocumentEvent.DocumentEventType.UPDATED)
                        .previousStatus(updatedMetadata.getStatus())
                        .newStatus(updatedMetadata.getStatus())
                        .initiatedBy(username)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        
        return mapToDTO(updatedMetadata);
    }

    @Override
    @Transactional
    public DocumentDTO updateDocumentStatus(Long id, DocumentStatus newStatus, String username, String comments) {
        DocumentMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        
        DocumentStatus previousStatus = metadata.getStatus();
        metadata.setStatus(newStatus);
        metadata.setLastModifiedBy(username);
        metadata.setLastModifiedAt(LocalDateTime.now());
        
        DocumentMetadata updatedMetadata = metadataRepository.save(metadata);
        
        // Publish event
        eventPublisher.publishDocumentEvent(
                DocumentEvent.builder()
                        .documentId(updatedMetadata.getId())
                        .documentTitle(updatedMetadata.getTitle())
                        .eventType(DocumentEvent.DocumentEventType.STATUS_CHANGED)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .initiatedBy(username)
                        .timestamp(LocalDateTime.now())
                        .comments(comments)
                        .build()
        );
        
        return mapToDTO(updatedMetadata);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        DocumentMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        
        // Delete content from MongoDB
        Long fileIdToDelete = Long.parseLong(metadata.getFileId());
        contentRepository.deleteById(fileIdToDelete);
        
        // Delete metadata from PostgreSQL
        metadataRepository.delete(metadata);
        
        // Publish event
        eventPublisher.publishDocumentEvent(
                DocumentEvent.builder()
                        .documentId(metadata.getId())
                        .documentTitle(metadata.getTitle())
                        .eventType(DocumentEvent.DocumentEventType.DELETED)
                        .previousStatus(metadata.getStatus())
                        .initiatedBy("system") // This should be the actual user
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
    
    private DocumentDTO mapToDTO(DocumentMetadata metadata) {
        return DocumentDTO.builder()
                .id(metadata.getId())
                .title(metadata.getTitle())
                .description(metadata.getDescription())
                .fileName(metadata.getFileName())
                .contentType(metadata.getContentType())
                .fileSize(metadata.getFileSize())
                .createdBy(metadata.getCreatedBy())
                .createdAt(metadata.getCreatedAt())
                .lastModifiedBy(metadata.getLastModifiedBy())
                .lastModifiedAt(metadata.getLastModifiedAt())
                .status(metadata.getStatus())
                .version(metadata.getVersion())
                .build();
    }
}
