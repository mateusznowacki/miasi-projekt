package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.medicalcare.domain.model.Visit;
import java.util.List;

public interface VisitQueryUseCase {
    List<Visit> getVisitsByPatient(PatientId patientId);
    List<Visit> getVisitsByDoctor(pl.MiASI.shared.domain.model.DoctorId doctorId);
}