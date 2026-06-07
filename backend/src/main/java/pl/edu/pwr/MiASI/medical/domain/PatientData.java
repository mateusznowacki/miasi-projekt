package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record PatientData(PatientId patientId, String email, String nationalId) {}
