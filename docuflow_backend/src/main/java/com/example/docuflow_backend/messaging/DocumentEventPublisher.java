package com.example.docuflow_backend.messaging;

import com.example.docuflow_backend.model.DocumentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventPublisher {
    
    private final PulsarClient pulsarClient;
    private final ObjectMapper objectMapper;
    
    private Producer<String> producer;
    
    @PostConstruct
    public void init() {
        try {
            producer = pulsarClient.newProducer(Schema.STRING)
                    .topic("persistent://public/default/document-events")
                    .create();
            log.info("Pulsar producer initialized successfully");
        } catch (PulsarClientException e) {
            log.error("Failed to initialize Pulsar producer", e);
            throw new RuntimeException("Failed to initialize Pulsar producer", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (producer != null) {
            try {
                producer.close();
                log.info("Pulsar producer closed successfully");
            } catch (PulsarClientException e) {
                log.error("Failed to close Pulsar producer", e);
            }
        }
    }
    
    public void publishDocumentEvent(DocumentEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            producer.send(eventJson);
            log.info("Published document event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish document event", e);
            throw new RuntimeException("Failed to publish document event", e);
        }
    }
}
