package com.example.docuflow_backend.messaging;

import com.example.docuflow_backend.model.DocumentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventConsumer {
    
    private final PulsarClient pulsarClient;
    private final ObjectMapper objectMapper;
    
    private Consumer<String> consumer;
    private ExecutorService executorService;
    private volatile boolean running = false;
    
    @PostConstruct
    public void init() {
        try {
            consumer = pulsarClient.newConsumer(Schema.STRING)
                    .topic("persistent://public/default/document-events")
                    .subscriptionName("document-events-subscription")
                    .subscriptionType(SubscriptionType.Shared)
                    .subscribe();
            
            executorService = Executors.newSingleThreadExecutor();
            running = true;
            
            executorService.submit(this::consumeMessages);
            
            log.info("Pulsar consumer initialized successfully");
        } catch (PulsarClientException e) {
            log.error("Failed to initialize Pulsar consumer", e);
            throw new RuntimeException("Failed to initialize Pulsar consumer", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        running = false;
        
        if (executorService != null) {
            executorService.shutdown();
        }
        
        if (consumer != null) {
            try {
                consumer.close();
                log.info("Pulsar consumer closed successfully");
            } catch (PulsarClientException e) {
                log.error("Failed to close Pulsar consumer", e);
            }
        }
    }
    
    private void consumeMessages() {
        while (running) {
            try {
                Message<String> message = consumer.receive();
                String payload = message.getValue();
                
                try {
                    DocumentEvent event = objectMapper.readValue(payload, DocumentEvent.class);
                    processDocumentEvent(event);
                    consumer.acknowledge(message);
                } catch (Exception e) {
                    log.error("Failed to process document event", e);
                    consumer.negativeAcknowledge(message);
                }
            } catch (Exception e) {
                log.error("Error receiving message from Pulsar", e);
            }
        }
    }
    
    private void processDocumentEvent(DocumentEvent event) {
        // In a real application, this would trigger notifications, logging, etc.
        log.info("Processing document event: {}", event);
        
        switch (event.getEventType()) {
            case CREATED:
                log.info("Document created: {}", event.getDocumentTitle());
                break;
            case UPDATED:
                log.info("Document updated: {}", event.getDocumentTitle());
                break;
            case STATUS_CHANGED:
                log.info("Document status changed from {} to {}: {}", 
                        event.getPreviousStatus(), 
                        event.getNewStatus(), 
                        event.getDocumentTitle());
                break;
            case DELETED:
                log.info("Document deleted: {}", event.getDocumentTitle());
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
