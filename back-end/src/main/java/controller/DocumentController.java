import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.docuflow.model.Document;
import com.docuflow.model.DocumentContent; // Import DocumentContent
import com.docuflow.repository.DocumentRepository;
import com.docuflow.repository.DocumentContentRepository; // Import DocumentContentRepository

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap; // Import HashMap
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentContentRepository documentContentRepository; // Inject DocumentContentRepository

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        // 1. Set timestamps
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        // 2. Set initial status (e.g., "DRAFT")
        document.setStatus("DRAFT");

        // 3. Handle document content (MongoDB)
        DocumentContent documentContent = new DocumentContent();
        documentContent.setContentType(document.getContentType()); // Assuming Document has getContentType()
        documentContent.setContent(document.getContent());       // Assuming Document has getContent()

        // You might want to add more metadata to documentContent here, e.g.,
        // documentContent.getMetadata().put("originalFilename", document.getOriginalFilename());

        DocumentContent savedContent = documentContentRepository.save(documentContent);

        // 4. Store MongoDB ID in PostgreSQL Document
        document.setMongoId(savedContent.getId()); // Assuming Document has a 'mongoId' field

        // 5. Save the document to PostgreSQL
        Document savedDocument = documentRepository.save(document);

        // 6. Return the saved document with HTTP status 201 (Created)
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        // 1. Retrieve document metadata from PostgreSQL
        Document document = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (document == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Retrieve document content from MongoDB (if mongoId exists)
        if (document.getMongoId() != null) {
            DocumentContent documentContent = documentContentRepository.findById(document.getMongoId()).orElse(null);
            if (documentContent != null) {
                document.setContentType(documentContent.getContentType());
                document.setContent(documentContent.getContent());
                // You might want to add more metadata from documentContent to document here
            }
        }

        // 4. Return the document with HTTP status 200 (OK)
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document updatedDocument) {
        // 1. Retrieve the existing document from PostgreSQL
        Document existingDocument = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (existingDocument == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Update the document metadata
        existingDocument.setTitle(updatedDocument.getTitle());
        existingDocument.setOwner(updatedDocument.getOwner());
        // Update other metadata fields as needed
        existingDocument.setUpdatedAt(LocalDateTime.now());

        // 4. Handle document content update (MongoDB)
        if (updatedDocument.getContent() != null) {
            if (existingDocument.getMongoId() != null) {
                // Update existing content in MongoDB
                DocumentContent existingContent = documentContentRepository.findById(existingDocument.getMongoId()).orElse(null);
                if (existingContent != null) {
                    existingContent.setContentType(updatedDocument.getContentType());
                    existingContent.setContent(updatedDocument.getContent());
                    documentContentRepository.save(existingContent);
                } else {
                    // Handle the case where the content ID is present in PostgreSQL but not in MongoDB
                    // This might indicate data inconsistency
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Create new content in MongoDB and store the ID in PostgreSQL
                DocumentContent newContent = new DocumentContent();
                newContent.setContentType(updatedDocument.getContentType());
                newContent.setContent(updatedDocument.getContent());
                DocumentContent savedContent = documentContentRepository.save(newContent);
                existingDocument.setMongoId(savedContent.getId());
            }
        }

        // 5. Save the updated document to PostgreSQL
        Document savedDocument = documentRepository.save(existingDocument);

        // 6. Return the updated document with HTTP status 200 (OK)
        return new ResponseEntity<>(savedDocument, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        // 1. Retrieve the existing document from PostgreSQL
        Document document = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (document == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Delete document content from MongoDB (if mongoId exists)
        if (document.getMongoId() != null) {
            documentContentRepository.deleteById(document.getMongoId());
        }

        // 4. Delete the document metadata from PostgreSQL
        documentRepository.deleteById(id);

        // 5. Return HTTP status 204 (No Content)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String status
    ) {
        // 1. Retrieve documents from PostgreSQL
        List<Document> documents;

        if (title != null || owner != null || status != null) {
            // 2. Implement filtering/search logic
            // This is a basic example; you might need more complex queries
            documents = documentRepository.findAll(
                    (root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (title != null) {
                            predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
                        }
                        if (owner != null) {
                            predicates.add(criteriaBuilder.like(root.get("owner"), "%" + owner + "%"));
                        }
                        if (status != null) {
                            predicates.add(criteriaBuilder.equal(root.get("status"), status));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
            );
        } else {
            // 3. Retrieve all documents if no filters are provided
            documents = documentRepository.findAll();
        }

        // 4. Return the list of documents with HTTP status 200 (OK)
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Document> submitDocument(@PathVariable Long id) {
        // 1. Retrieve the document from PostgreSQL
        Document document = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (document == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Check document state (example: can only submit a DRAFT)
        if (!document.getStatus().equals("DRAFT")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        // 4. Update document state
        document.setStatus("SUBMITTED");
        document.setUpdatedAt(LocalDateTime.now());
        Document savedDocument = documentRepository.save(document);

        // 5. Publish Pulsar event (TODO: Implement Pulsar integration)
        // Example:  pulsarService.publishEvent(new DocumentEvent(document.getId(), "SUBMITTED"));

        // 6. Return the updated document with HTTP status 200 (OK)
        return new ResponseEntity<>(savedDocument, HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Document> approveDocument(@PathVariable Long id) {
        // 1. Retrieve the document from PostgreSQL
        Document document = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (document == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Check document state (example: can only approve a SUBMITTED document)
        if (!document.getStatus().equals("SUBMITTED")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        // 4. Update document state
        document.setStatus("APPROVED");
        document.setUpdatedAt(LocalDateTime.now());
        Document savedDocument = documentRepository.save(document);

        // 5. Publish Pulsar event (TODO: Implement Pulsar integration)
        // Example:  pulsarService.publishEvent(new DocumentEvent(document.getId(), "APPROVED"));

        // 6. Return the updated document with HTTP status 200 (OK)
        return new ResponseEntity<>(savedDocument, HttpStatus.OK);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Document> rejectDocument(@PathVariable Long id) {
        // 1. Retrieve the document from PostgreSQL
        Document document = documentRepository.findById(id).orElse(null);

        // 2. Check if the document exists
        if (document == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // 3. Check document state (example: can only reject a SUBMITTED document)
        if (!document.getStatus().equals("SUBMITTED")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        // 4. Update document state
        document.setStatus("REJECTED");
        document.setUpdatedAt(LocalDateTime.now());
        Document savedDocument = documentRepository.save(document);

        // 5. Publish Pulsar event (TODO: Implement Pulsar integration)
        // Example:  pulsarService.publishEvent(new DocumentEvent(document.getId(), "REJECTED"));

        // 6. Return the updated document with HTTP status 200 (OK)
        return new ResponseEntity<>(savedDocument, HttpStatus.OK);
    }
}