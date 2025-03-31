import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "document_contents") // Changed collection name to be more specific
public class DocumentContent {

    @Id
    private String id;

    private String contentType;
    private byte[] content;
    private Map<String, Object> metadata;

    // Constructors, getters, and setters
    public DocumentContent() {
    }

    public DocumentContent(String contentType, byte[] content, Map<String, Object> metadata) {
        this.contentType = contentType;
        this.content = content;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}