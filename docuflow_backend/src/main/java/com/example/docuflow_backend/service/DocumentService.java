package com.example.docuflow_backend.service;

import com.example.docuflow_backend.dto.DocumentDTO;
import com.example.docuflow_backend.model.DocumentStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    
    DocumentDTO uploadDocument(MultipartFile file, String title, String description, String username);
    
    DocumentDTO getDocument(Long id);
    
    byte[] getDocumentContent(Long id);
    
    List<DocumentDTO> getAllDocuments();
    
    List<DocumentDTO> getDocumentsByUser(String username);
    
    List<DocumentDTO> getDocumentsByStatus(DocumentStatus status);
    
    List<DocumentDTO> searchDocuments(String query);
    
    DocumentDTO updateDocumentMetadata(Long id, String title, String description, String username);
    
    DocumentDTO updateDocumentStatus(Long id, DocumentStatus newStatus, String username, String comments);
    
    void deleteDocument(Long id);
}
