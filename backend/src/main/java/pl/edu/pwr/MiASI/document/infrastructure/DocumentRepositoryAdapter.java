package pl.edu.pwr.MiASI.document.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.document.domain.*;
import java.util.Optional;

@Component
public class DocumentRepositoryAdapter implements DocumentRepository {
    private final SpringDataDocumentRepository repository;

    public DocumentRepositoryAdapter(SpringDataDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Document document) {
        DocumentJpaEntity entity = new DocumentJpaEntity(
            document.getId().id(),
            document.getReferencjaPliku().url(),
            document.getTypPliku().name(),
            document.getStatusOcr().name(),
            document.getMetadane() != null ? document.getMetadane().extractedText() : null,
            document.getMetadane() != null ? document.getMetadane().issueDate() : null
        );
        repository.save(entity);
    }

    @Override
    public Optional<Document> findById(DocumentId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    private Document toDomain(DocumentJpaEntity entity) {
        Document document = new Document(
            new DocumentId(entity.getId()),
            new FileReference(entity.getReferencjaPliku()),
            FileType.valueOf(entity.getTypPliku())
        );
        
        if (entity.getWyekstrahowanyTekst() != null) {
            document.enrichMetadata(new Metadata(entity.getWyekstrahowanyTekst(), entity.getDataWystawienia()));
        }
        
        return document;
    }
}
