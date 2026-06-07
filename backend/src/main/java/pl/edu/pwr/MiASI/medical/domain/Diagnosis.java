package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record Diagnosis(String kodICD10, String description) {}
