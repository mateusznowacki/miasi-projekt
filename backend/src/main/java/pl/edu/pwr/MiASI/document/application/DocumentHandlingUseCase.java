package pl.edu.pwr.MiASI.document.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.document.domain.*;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Service
public class DocumentHandlingUseCase {
    private final DocumentRepository dokumentRepository;
    private final DomainEventPublisher eventPublisher;

    public DocumentHandlingUseCase(DocumentRepository dokumentRepository, DomainEventPublisher eventPublisher) {
        this.dokumentRepository = dokumentRepository;
        this.eventPublisher = eventPublisher;
    }

    public void uploadDocument(FileReference fileReference, FileType fileType) {
        Document document = Document.upload(fileReference, fileType);
        dokumentRepository.save(document);
        eventPublisher.publish(new DocumentUploaded(document.getId(), fileReference));
    }
    
    public void enrichAfterOcr(DocumentId dokumentId, Metadata metadata) {
        Document document = dokumentRepository.findById(dokumentId)
            .orElseThrow(() -> new IllegalArgumentException("Document nie istnieje"));
            
        document.enrichMetadata(metadata);
        dokumentRepository.save(document);
        
        eventPublisher.publish(new MetadataExtracted(document.getId(), metadata));
    }
}
