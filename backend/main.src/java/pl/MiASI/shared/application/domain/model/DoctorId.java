package pl.MiASI.shared.application.domain.model;

import java.util.UUID;

public record DoctorId(UUID value) {
    public DoctorId() {
        this(UUID.randomUUID());
    }
}