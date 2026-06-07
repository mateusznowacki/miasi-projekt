package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record FacilityId(UUID id) {
    public static FacilityId generate() {
        return new FacilityId(UUID.randomUUID());
    }
}
