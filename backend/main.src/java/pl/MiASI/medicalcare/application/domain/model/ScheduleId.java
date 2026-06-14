package pl.MiASI.medicalcare.application.domain.model;

import java.util.UUID;

public record ScheduleId(UUID value) {
    public ScheduleId() {
        this(UUID.randomUUID());
    }
}