package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record AppointmentId(UUID id) {
    public static AppointmentId generate() { return new AppointmentId(UUID.randomUUID()); }
}
