package pl.edu.pwr.MiASI.document.domain;
import pl.edu.pwr.MiASI.shared.domain.DomainEvent;

public record DocumentUploaded(DocumentId dokumentId, FileReference fileReference) implements DomainEvent {}
