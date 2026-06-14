package pl.MiASI.medicalcare.application.port.out;

import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.medicalcare.application.domain.model.VisitId;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;
import java.util.Optional;

public interface VisitRepository {
    void save(Visit visit);

    Optional<Visit> findById(VisitId visitId);

    List<Visit> findByPatientId(PatientId patientId);

    List<Visit> findByDoctorId(DoctorId doctorId);
}