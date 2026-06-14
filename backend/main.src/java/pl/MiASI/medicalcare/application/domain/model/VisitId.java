package pl.MiASI.medicalcare.application.domain.model;

import java.util.UUID;

public record VisitId(UUID value) {
    public VisitId() {
        this(UUID.randomUUID());
    }
}