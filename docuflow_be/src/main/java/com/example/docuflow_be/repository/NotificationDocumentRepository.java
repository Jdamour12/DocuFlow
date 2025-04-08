package com.example.docuflow_be.repository;

import com.example.docuflow_be.document.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDocumentRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserIdAndIsReadFalseOrderByTimestampDesc(String userId);
}