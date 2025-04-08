package com.example.docuflow_be.controller;

import com.example.docuflow_be.dto.ReadDocumentResponse;
import com.example.docuflow_be.dto.SubmitDocumentRequest;
import com.example.docuflow_be.dto.DocumentHistoryResponse;
import com.example.docuflow_be.service.DocumentService;
import com.example.docuflow_be.dto.NotificationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@Valid @RequestBody SubmitDocumentRequest submitRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String uid = userDetails.getUsername();

            if ("submit1".equals(uid) || "admin.user".equals(uid)) {
                documentService.submitDocument(submitRequest.getContent(), submitRequest.getTitle(), submitRequest.getDocumentType(), uid);
                return ResponseEntity.ok("Document submission initiated by user: " + uid);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to submit documents.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }

    @PostMapping("/review/{documentId}")
    public ResponseEntity<String> review(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid documentId format") String documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String uid = userDetails.getUsername();

            if ("review1".equals(uid) || "review2".equals(uid) || "admin.user".equals(uid)) {
                try {
                    documentService.reviewDocument(documentId);
                    return ResponseEntity.ok("Document with ID: " + documentId + " is now under review.");
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during review.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to review documents.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }

    @PostMapping("/approve/{documentId}")
    public ResponseEntity<String> approve(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid documentId format") String documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String uid = userDetails.getUsername();

            if ("approve1".equals(uid) || "approve2".equals(uid) || "admin.user".equals(uid)) {
                try {
                    documentService.approveDocument(documentId);
                    return ResponseEntity.ok("Document with ID: " + documentId + " has been approved.");
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                } catch (IllegalStateException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during approval.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to approve documents.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }

    @PostMapping("/reject/{documentId}")
    public ResponseEntity<String> reject(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid documentId format") String documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String uid = userDetails.getUsername();

            if ("review1".equals(uid) || "review2".equals(uid) || "approve1".equals(uid) || "approve2".equals(uid) || "admin.user".equals(uid)) {
                try {
                    documentService.rejectDocument(documentId);
                    return ResponseEntity.ok("Document with ID: " + documentId + " has been rejected.");
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                } catch (IllegalStateException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during rejection.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to reject documents.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ReadDocumentResponse> getDocument(@PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid documentId format") String documentId) {
        ReadDocumentResponse document = documentService.readDocument(documentId);
        if (document != null) {
            return ResponseEntity.ok(document);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReadDocumentResponse>> getAllDocuments(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "status", required = false) String status) {
        List<ReadDocumentResponse> documents = documentService.searchDocuments(title, documentType, status);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}/history")
    public ResponseEntity<List<DocumentHistoryResponse>> getDocumentHistory(
            @PathVariable @NotNull @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid documentId format") String documentId) {
        List<DocumentHistoryResponse> history = documentService.getDocumentHistory(documentId);
        return ResponseEntity.ok(history);
    }

    @RequestMapping("/api/notifications") // Changed the base path to /api/notifications for clarity
public class NotificationController { // Created a new controller for notifications

    private final DocumentService documentService;

    public NotificationController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        String userId = documentService.getCurrentUserUid();
        if (userId != null) {
            List<NotificationResponse> notifications = documentService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } else {
            return ResponseEntity.status(401).build(); // Unauthorized if no user is authenticated
        }
    }

    @PostMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        // Implementation to mark a specific notification as read will come later
        System.out.println("Mark as read requested for notification ID: " + notificationId);
        return ResponseEntity.ok().build();
    }
}
}