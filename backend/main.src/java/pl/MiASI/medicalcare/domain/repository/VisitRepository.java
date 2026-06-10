package pl.MiASI.medicalcare.domain.repository;

import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.model.VisitId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.shared.domain.model.DoctorId;
import java.util.Optional;
import java.util.List;

public interface VisitRepository {
    void save(Visit visit);
    Optional<Visit> findById(VisitId visitId);
    List<Visit> findByPatientId(PatientId patientId);
    List<Visit> findByDoctorId(DoctorId doctorId);
}