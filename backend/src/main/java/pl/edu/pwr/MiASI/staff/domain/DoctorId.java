package pl.edu.pwr.MiASI.staff.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record DoctorId(UUID id) {
    public static DoctorId generate() {
        return new DoctorId(UUID.randomUUID());
    }
}
