package pl.MiASI.shared.domain.model;

import java.util.UUID;

public record PatientId(UUID value) {
    public PatientId() {
        this(UUID.randomUUID());
    }
}