package com.example.docuflow_be.repository;

import com.example.docuflow_be.entity.DocumentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentHistoryEntityRepository extends JpaRepository<DocumentHistoryEntity, Long> {
    List<DocumentHistoryEntity> findByDocumentIdOrderByTimestampAsc(UUID documentId);
}