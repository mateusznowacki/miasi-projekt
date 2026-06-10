package pl.MiASI.medicalcare.domain.model;

import java.util.UUID;

public record VisitId(UUID value) {
    public VisitId() {
        this(UUID.randomUUID());
    }
}