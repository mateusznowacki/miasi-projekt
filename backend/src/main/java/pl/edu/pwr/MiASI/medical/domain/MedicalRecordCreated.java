package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.DomainEvent;

public record MedicalRecordCreated(RecordId recordId, AppointmentId wizytaId) implements DomainEvent {}
