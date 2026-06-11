package pl.MiASI.patient.domain.repository;

import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.shared.domain.model.PatientId;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    void save(Patient patient);

    Optional<Patient> findById(PatientId id);

    List<Patient> findAll();

    boolean existsByPesel(String pesel);
}