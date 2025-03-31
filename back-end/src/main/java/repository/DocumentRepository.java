import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.docuflow.model.Document; // Replace with your actual package name

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // You can add custom query methods here if needed
}