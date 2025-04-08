package com.example.docuflow_be.repository;

import com.example.docuflow_be.document.DocumentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentDocumentRepository extends MongoRepository<DocumentDocument, UUID> {
    // You can add custom query methods here if needed in the future
}