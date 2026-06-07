package pl.edu.pwr.MiASI.document.domain;
import java.util.Optional;

public interface DocumentRepository {
    void save(Document document);
    Optional<Document> findById(DocumentId id);
}
