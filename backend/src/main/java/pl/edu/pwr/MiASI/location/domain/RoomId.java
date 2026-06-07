package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record RoomId(UUID id) {
    public static RoomId generate() {
        return new RoomId(UUID.randomUUID());
    }
}
