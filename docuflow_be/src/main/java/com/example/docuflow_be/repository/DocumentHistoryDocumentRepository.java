package com.example.docuflow_be.repository;

import com.example.docuflow_be.document.DocumentHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentHistoryDocumentRepository extends MongoRepository<DocumentHistoryDocument, String> {
    List<DocumentHistoryDocument> findByDocumentIdOrderByTimestampAsc(UUID documentId);
}