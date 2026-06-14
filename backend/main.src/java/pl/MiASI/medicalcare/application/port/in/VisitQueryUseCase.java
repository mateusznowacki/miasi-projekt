package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;

public interface VisitQueryUseCase {
    List<Visit> getVisitsByPatient(PatientId patientId);

    List<Visit> getVisitsByDoctor(pl.MiASI.shared.application.domain.model.DoctorId doctorId);
}