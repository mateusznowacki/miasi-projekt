package pl.edu.pwr.MiASI.document.domain;
import pl.edu.pwr.MiASI.shared.domain.DomainEvent;

public record MetadataExtracted(DocumentId dokumentId, Metadata metadata) implements DomainEvent {}
