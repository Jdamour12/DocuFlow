package com.example.docuflow_backend.controller;

import com.example.docuflow_backend.dto.DocumentDTO;
import com.example.docuflow_backend.model.DocumentStatus;
import com.example.docuflow_backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        
        // In a real application, get the username from security context
        String username = "current-user"; // Placeholder
        
        DocumentDTO document = documentService.uploadDocument(file, title, description, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        DocumentDTO document = documentService.getDocument(id);
        return ResponseEntity.ok(document);
    }
    
    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getDocumentContent(@PathVariable Long id) {
        DocumentDTO document = documentService.getDocument(id);
        byte[] content = documentService.getDocumentContent(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(document.getContentType()));
        headers.setContentDispositionFormData("attachment", document.getFileName());
        
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByUser(@PathVariable String username) {
        List<DocumentDTO> documents = documentService.getDocumentsByUser(username);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByStatus(@PathVariable DocumentStatus status) {
        List<DocumentDTO> documents = documentService.getDocumentsByStatus(status);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam String query) {
        List<DocumentDTO> documents = documentService.searchDocuments(query);
        return ResponseEntity.ok(documents);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocumentMetadata(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        
        // In a real application, get the username from security context
        String username = "current-user"; // Placeholder
        
        DocumentDTO document = documentService.updateDocumentMetadata(id, title, description, username);
        return ResponseEntity.ok(document);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<DocumentDTO> updateDocumentStatus(
            @PathVariable Long id,
            @RequestParam DocumentStatus status,
            @RequestParam(value = "comments", required = false) String comments) {
        
        // In a real application, get the username from security context
        String username = "current-user"; // Placeholder
        
        DocumentDTO document = documentService.updateDocumentStatus(id, status, username, comments);
        return ResponseEntity.ok(document);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
