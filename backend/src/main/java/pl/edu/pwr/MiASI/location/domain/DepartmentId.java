package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record DepartmentId(UUID id) {
    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID());
    }
}
