package pl.MiASI.medicalcare.domain.model;

import java.util.UUID;

public record SlotId(UUID value) {
    public SlotId() {
        this(UUID.randomUUID());
    }
}