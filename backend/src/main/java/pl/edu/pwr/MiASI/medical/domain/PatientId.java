package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record PatientId(UUID id) {
    public static PatientId generate() { return new PatientId(UUID.randomUUID()); }
}
