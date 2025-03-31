import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.docuflow.model.DocumentContent; // Replace with your actual package name

@Repository
public interface DocumentContentRepository extends MongoRepository<DocumentContent, String> {
    // You can add custom query methods here if needed
}