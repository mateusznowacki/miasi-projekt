package pl.MiASI.medicalcare.domain.model;

import java.util.UUID;

public record ScheduleId(UUID value) {
    public ScheduleId() {
        this(UUID.randomUUID());
    }
}