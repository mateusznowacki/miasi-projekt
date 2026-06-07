package pl.edu.pwr.MiASI.document.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record DocumentId(UUID id) {
    public static DocumentId generate() { return new DocumentId(UUID.randomUUID()); }
}
