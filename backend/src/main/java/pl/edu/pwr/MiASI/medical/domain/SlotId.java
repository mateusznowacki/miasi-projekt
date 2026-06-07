package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record SlotId(UUID id) {
    public static SlotId generate() { return new SlotId(UUID.randomUUID()); }
}
