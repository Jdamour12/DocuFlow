package com.example.docuflow_be.service;

import jakarta.persistence.criteria.Predicate;
import java.util.stream.Stream;
import com.example.docuflow_be.document.DocumentDocument;
import com.example.docuflow_be.document.DocumentHistoryDocument;
import com.example.docuflow_be.document.NotificationDocument;
import com.example.docuflow_be.entity.DocumentEntity;
import com.example.docuflow_be.entity.DocumentHistoryEntity;
import com.example.docuflow_be.entity.NotificationEntity;
import com.example.docuflow_be.dto.ReadDocumentResponse;
import com.example.docuflow_be.model.DocumentStatus;
import com.example.docuflow_be.repository.DocumentEntityRepository;
import com.example.docuflow_be.repository.DocumentDocumentRepository;
import com.example.docuflow_be.repository.DocumentHistoryEntityRepository;
import com.example.docuflow_be.repository.DocumentHistoryDocumentRepository;
import com.example.docuflow_be.dto.DocumentHistoryResponse;
import com.example.docuflow_be.dto.NotificationResponse;
import com.example.docuflow_be.repository.NotificationEntityRepository;
import com.example.docuflow_be.repository.NotificationDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private static final String SUBMITTER_UID = "submit1";
    private static final String REVIEWER_UID_1 = "review1";
    private static final String REVIEWER_UID_2 = "review2";
    private static final String APPROVER_UID_1 = "approve1";
    private static final String APPROVER_UID_2 = "approve2";
    private static final String ADMIN_UID = "admin.user";

    private static final List<String> SUBMITTER_UIDS = List.of(SUBMITTER_UID, ADMIN_UID);
    private static final List<String> REVIEWER_UIDS = List.of(REVIEWER_UID_1, REVIEWER_UID_2, ADMIN_UID);
    private static final List<String> APPROVER_UIDS = List.of(APPROVER_UID_1, APPROVER_UID_2, ADMIN_UID);
    private static final List<String> REJECTOR_UIDS = List.of(REVIEWER_UID_1, REVIEWER_UID_2, APPROVER_UID_1, APPROVER_UID_2, ADMIN_UID);

    private final DocumentEntityRepository postgresRepository;
    private final DocumentDocumentRepository mongoRepository;
    private final DocumentHistoryEntityRepository documentHistoryEntityRepository;
    private final DocumentHistoryDocumentRepository documentHistoryDocumentRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final NotificationDocumentRepository notificationDocumentRepository;

    @Autowired
    public DocumentService(DocumentEntityRepository postgresRepository, DocumentDocumentRepository mongoRepository,
                DocumentHistoryEntityRepository documentHistoryEntityRepository,
                DocumentHistoryDocumentRepository documentHistoryDocumentRepository,
                NotificationEntityRepository notificationEntityRepository,
                NotificationDocumentRepository notificationDocumentRepository) {
        this.postgresRepository = postgresRepository;
        this.mongoRepository = mongoRepository;
        this.documentHistoryEntityRepository = documentHistoryEntityRepository;
        this.documentHistoryDocumentRepository = documentHistoryDocumentRepository;
        this.notificationEntityRepository = notificationEntityRepository;
        this.notificationDocumentRepository = notificationDocumentRepository;
    }

    public void submitDocument(String documentData, String title, String documentType, String submittingUserUid) {
        if (SUBMITTER_UIDS.contains(submittingUserUid)) {
            UUID documentId = UUID.randomUUID();
            LocalDateTime submissionTime = LocalDateTime.now();
            DocumentStatus initialStatus = DocumentStatus.SUBMITTED;

            DocumentEntity postgresEntity = new DocumentEntity(
                    documentData,
                    title,
                    documentType,
                    submittingUserUid,
                    submissionTime,
                    initialStatus
            );
            postgresEntity.setId(documentId);
            postgresRepository.save(postgresEntity); // Save to PostgreSQL

            DocumentDocument mongoDocument = new DocumentDocument(
                    documentData,
                    title,
                    documentType,
                    submittingUserUid,
                    submissionTime,
                    initialStatus
            );
            mongoDocument.setId(documentId);
            mongoRepository.save(mongoDocument); // Save to MongoDB

            recordHistory(documentId, "SUBMITTED", submittingUserUid, submissionTime);

            // Notify reviewers
            REVIEWER_UIDS.forEach(reviewerUid -> createNotification(
                    reviewerUid,
                    documentId.toString(),
                    "New document submitted: '" + title + "' by " + submittingUserUid,
                    submissionTime
            ));

            System.out.println("Document submitted with ID: " + documentId + " by user: " + submittingUserUid + " at: " + submissionTime);
            System.out.println("Title: " + title + ", Type: " + documentType);
            System.out.println("Document saved to PostgreSQL.");
            System.out.println("Document saved to MongoDB.");
            System.out.println("Document history recorded: SUBMITTED");
            System.out.println("Reviewers notified.");
        } else {
            throw new AccessDeniedException("You are not authorized to submit documents.");
        }
    }

    public void reviewDocument(String documentId) {
        String reviewerUid = getCurrentUserUid();
        if (REVIEWER_UIDS.contains(reviewerUid)) {
            UUID id = UUID.fromString(documentId);
            Optional<DocumentEntity> postgresEntityOptional = postgresRepository.findById(id);
            Optional<DocumentDocument> mongoDocumentOptional = mongoRepository.findById(id);

            if (postgresEntityOptional.isPresent() && mongoDocumentOptional.isPresent()) {
                DocumentEntity documentEntity = postgresEntityOptional.get();
                DocumentDocument documentDocument = mongoDocumentOptional.get();
                documentEntity.setStatus(DocumentStatus.UNDER_REVIEW);
                documentDocument.setStatus(DocumentStatus.UNDER_REVIEW);
                postgresRepository.save(documentEntity);
                mongoRepository.save(documentDocument);

                recordHistory(id, "REVIEWED", reviewerUid, LocalDateTime.now());

                // Notify submitter and approvers
                String submitterUid = documentEntity.getSubmitterUid();
                if (SUBMITTER_UIDS.contains(submitterUid)) {
                    createNotification(
                            submitterUid,
                            documentId,
                            "Your document '" + documentEntity.getTitle() + "' is now under review by " + reviewerUid,
                            LocalDateTime.now()
                    );
                }
                APPROVER_UIDS.forEach(approverUid -> createNotification(
                        approverUid,
                        documentId,
                        "Document '" + documentEntity.getTitle() + "' is ready for approval",
                        LocalDateTime.now()
                ));

                System.out.println("Document with ID: " + documentId + " is now under review by user: " + reviewerUid);
                System.out.println("Document history recorded: REVIEWED");
                System.out.println("Submitter and approvers notified.");
            } else {
                throw new IllegalArgumentException("Document with ID " + documentId + " not found for review.");
            }
        } else {
            throw new AccessDeniedException("You are not authorized to review documents.");
        }
    }

    public void approveDocument(String documentId) {
        String approverUid = getCurrentUserUid();
        if (APPROVER_UIDS.contains(approverUid)) {
            UUID id = UUID.fromString(documentId);
            Optional<DocumentEntity> postgresEntityOptional = postgresRepository.findById(id);
            Optional<DocumentDocument> mongoDocumentOptional = mongoRepository.findById(id);

            if (postgresEntityOptional.isPresent() && mongoDocumentOptional.isPresent()) {
                DocumentEntity documentEntity = postgresEntityOptional.get();
                DocumentDocument documentDocument = mongoDocumentOptional.get();
                if (documentEntity.getStatus() == DocumentStatus.UNDER_REVIEW) {
                    documentEntity.setStatus(DocumentStatus.APPROVED);
                    documentDocument.setStatus(DocumentStatus.APPROVED);
                    postgresRepository.save(documentEntity);
                    mongoRepository.save(documentDocument);

                    recordHistory(id, "APPROVED", approverUid, LocalDateTime.now());

                    // Notify submitter
                    String submitterUid = documentEntity.getSubmitterUid();
                    if (SUBMITTER_UIDS.contains(submitterUid)) {
                        createNotification(
                                submitterUid,
                                documentId,
                                "Your document '" + documentEntity.getTitle() + "' has been approved by " + approverUid,
                                LocalDateTime.now()
                        );
                    }

                    System.out.println("Document with ID: " + documentId + " has been approved by user: " + approverUid);
                    System.out.println("Document history recorded: APPROVED");
                    System.out.println("Submitter notified.");
                } else {
                    throw new IllegalStateException("Document with ID " + documentId + " is not in the 'UNDER_REVIEW' status and cannot be approved.");
                }
            } else {
                throw new IllegalArgumentException("Document with ID " + documentId + " not found for approval.");
            }
        } else {
            throw new AccessDeniedException("You are not authorized to approve documents.");
        }
    }

    public void rejectDocument(String documentId) {
        String rejectorUid = getCurrentUserUid();
        if (REJECTOR_UIDS.contains(rejectorUid)) {
            UUID id = UUID.fromString(documentId);
            Optional<DocumentEntity> postgresEntityOptional = postgresRepository.findById(id);
            Optional<DocumentDocument> mongoDocumentOptional = mongoRepository.findById(id);

            if (postgresEntityOptional.isPresent() && mongoDocumentOptional.isPresent()) {
                DocumentEntity documentEntity = postgresEntityOptional.get();
                DocumentDocument documentDocument = mongoDocumentOptional.get();
                if (documentEntity.getStatus() == DocumentStatus.SUBMITTED || documentEntity.getStatus() == DocumentStatus.UNDER_REVIEW) {
                    documentEntity.setStatus(DocumentStatus.REJECTED);
                    documentDocument.setStatus(DocumentStatus.REJECTED);
                    postgresRepository.save(documentEntity);
                    mongoRepository.save(documentDocument);

                    recordHistory(id, "REJECTED", rejectorUid, LocalDateTime.now());

                    // Notify submitter
                    String submitterUid = documentEntity.getSubmitterUid();
                    if (SUBMITTER_UIDS.contains(submitterUid)) {
                        createNotification(
                                submitterUid,
                                documentId,
                                "Your document '" + documentEntity.getTitle() + "' has been rejected by " + rejectorUid,
                                LocalDateTime.now()
                        );
                    }

                    System.out.println("Document with ID: " + documentId + " has been rejected by user: " + rejectorUid);
                    System.out.println("Document history recorded: REJECTED");
                    System.out.println("Submitter notified.");
                } else {
                    throw new IllegalStateException("Document with ID " + documentId + " cannot be rejected in its current status: " + documentEntity.getStatus());
                }
            } else {
                throw new IllegalArgumentException("Document with ID " + documentId + " not found for rejection.");
            }
        } else {
            throw new AccessDeniedException("You are not authorized to reject documents.");
        }
    }

    public ReadDocumentResponse readDocument(String documentId) {
        UUID id = UUID.fromString(documentId);
        Optional<DocumentEntity> postgresEntityOptional = postgresRepository.findById(id);

        if (postgresEntityOptional.isPresent()) {
            DocumentEntity entity = postgresEntityOptional.get();
            return new ReadDocumentResponse(
                    entity.getId(),
                    entity.getContent(),
                    entity.getTitle(),
                    entity.getDocumentType(),
                    entity.getSubmitterUid(),
                    entity.getSubmissionTime(),
                    entity.getStatus().toString()
            );
        } else {
            return null;
        }
    }

    public List<ReadDocumentResponse> getAllDocuments() {
        List<ReadDocumentResponse> responses = new ArrayList<>();
        List<DocumentEntity> allEntities = postgresRepository.findAll();
        for (DocumentEntity entity : allEntities) {
            responses.add(new ReadDocumentResponse(
                    entity.getId(),
                    entity.getContent(),
                    entity.getTitle(),
                    entity.getDocumentType(),
                    entity.getSubmitterUid(),
                    entity.getSubmissionTime(),
                    entity.getStatus().toString()
            ));
        }
        return responses;
    }

    public List<ReadDocumentResponse> searchDocuments(String title, String documentType, String status) {
        Specification<DocumentEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%"));
            }

            if (documentType != null && !documentType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("documentType"), documentType.trim()));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    DocumentStatus documentStatus = DocumentStatus.valueOf(status.trim().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), documentStatus));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status values
                    System.err.println("Invalid status value: " + status);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<DocumentEntity> searchResults = postgresRepository.findAll(spec);
        return searchResults.stream()
                .map(entity -> new ReadDocumentResponse(
                        entity.getId(),
                        entity.getContent(),
                        entity.getTitle(),
                        entity.getDocumentType(),
                        entity.getSubmitterUid(),
                        entity.getSubmissionTime(),
                        entity.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    private void recordHistory(UUID documentId, String action, String performedBy, LocalDateTime timestamp) {
        DocumentHistoryEntity historyEntity = new DocumentHistoryEntity(documentId, action, performedBy, timestamp);
        documentHistoryEntityRepository.save(historyEntity);

        DocumentHistoryDocument historyDocument = new DocumentHistoryDocument(documentId, action, performedBy, timestamp);
        documentHistoryDocumentRepository.save(historyDocument);
    }

    public String getCurrentUserUid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    public List<DocumentHistoryResponse> getDocumentHistory(String documentId) {
        UUID id = UUID.fromString(documentId);

        List<DocumentHistoryEntity> postgresHistory = documentHistoryEntityRepository.findByDocumentIdOrderByTimestampAsc(id);
        List<DocumentHistoryDocument> mongoHistory = documentHistoryDocumentRepository.findByDocumentIdOrderByTimestampAsc(id);

        // Combine and sort history records (optional, but good for consistency)
        return Stream.concat(
                postgresHistory.stream().map(entity -> new DocumentHistoryResponse(entity.getAction(), entity.getPerformedBy(), entity.getTimestamp())),
                mongoHistory.stream().map(document -> new DocumentHistoryResponse(document.getAction(), document.getPerformedBy(), document.getTimestamp()))
        )
                .sorted((h1, h2) -> h1.getTimestamp().compareTo(h2.getTimestamp()))
                .collect(Collectors.toList());
    }

    private void createNotification(String userId, String documentId, String message, LocalDateTime timestamp) {
        NotificationEntity notificationEntity = new NotificationEntity(userId, documentId, message, timestamp);
        notificationEntityRepository.save(notificationEntity);

        NotificationDocument notificationDocument = new NotificationDocument(userId, documentId, message, timestamp);
        notificationDocumentRepository.save(notificationDocument);
    }

    public List<NotificationResponse> getUserNotifications(String userId) {
        List<NotificationEntity> postgresNotifications = notificationEntityRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        List<NotificationDocument> mongoNotifications = notificationDocumentRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);

        List<NotificationResponse> responses = new ArrayList<>();

        // Map PostgreSQL notifications to DTOs
        for (NotificationEntity entity : postgresNotifications) {
            responses.add(new NotificationResponse(
                    String.valueOf(entity.getId()), // Convert Long ID to String
                    entity.getDocumentId(),
                    entity.getMessage(),
                    entity.getTimestamp(),
                    entity.isRead()
            ));
        }

        // Map MongoDB notifications to DTOs
        for (NotificationDocument document : mongoNotifications) {
            // Check if a notification with the same ID (from MongoDB) already exists in the responses
            // to avoid duplicates if both databases have the same notification.
            boolean exists = false;
            for (NotificationResponse response : responses) {
                if (response.getId() != null && response.getId().equals(document.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                responses.add(new NotificationResponse(
                        document.getId(),
                        document.getDocumentId(),
                        document.getMessage(),
                        document.getTimestamp(),
                        document.isRead()
                ));
            }
        }

        // Sort the combined list by timestamp in descending order (newest first)
        responses.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

        return responses;
    }
    
}