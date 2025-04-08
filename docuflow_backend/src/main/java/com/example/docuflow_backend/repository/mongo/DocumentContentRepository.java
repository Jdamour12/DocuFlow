package com.example.docuflow_backend.repository.mongo;

import com.example.docuflow_backend.model.DocumentContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentContentRepository extends MongoRepository<DocumentContent, Long> {
    
    Optional<DocumentContent> findByMetadataId(Long metadataId);
    
    void deleteByMetadataId(Long metadataId);
}
