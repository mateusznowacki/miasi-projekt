package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record ScheduleId(UUID id) {
    public static ScheduleId generate() { return new ScheduleId(UUID.randomUUID()); }
}
