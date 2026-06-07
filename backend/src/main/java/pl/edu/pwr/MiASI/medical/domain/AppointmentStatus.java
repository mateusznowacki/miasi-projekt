package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public enum AppointmentStatus {
    RESERVED,
    CANCELLED,
    COMPLETED
}
