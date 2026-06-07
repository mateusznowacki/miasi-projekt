package pl.edu.pwr.MiASI.medical.domain;

import java.util.Optional;

public interface IdentityPort {
    Optional<PatientData> getPatientData(PatientId patientId);
}
