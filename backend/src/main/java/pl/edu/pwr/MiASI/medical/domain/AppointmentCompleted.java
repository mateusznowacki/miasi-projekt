package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.DomainEvent;

public record AppointmentCompleted(AppointmentId wizytaId) implements DomainEvent {}
