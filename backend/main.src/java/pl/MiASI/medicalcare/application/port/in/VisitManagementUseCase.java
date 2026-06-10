package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.medicalcare.domain.model.ConsultationType;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.VisitId;

import java.util.List;

public interface VisitManagementUseCase {
    VisitId reserveVisit(PatientId patientId, DoctorId doctorId, ConsultationType type, List<SlotId> slotIds);
    void cancelVisit(VisitId visitId);
    void completeVisit(VisitId visitId);
}