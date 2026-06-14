package pl.MiASI.patient.application.port.out;

import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    void save(Patient patient);

    Optional<Patient> findById(PatientId id);

    List<Patient> findAll();

    boolean existsByPesel(String pesel);
}