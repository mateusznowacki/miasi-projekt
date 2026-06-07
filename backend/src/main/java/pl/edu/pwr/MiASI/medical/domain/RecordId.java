package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record RecordId(UUID id) {
    public static RecordId generate() { return new RecordId(UUID.randomUUID()); }
}
