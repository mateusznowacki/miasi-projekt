package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.application.domain.model.ConsultationType;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.VisitId;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;

public interface VisitManagementUseCase {
    VisitId reserveVisit(PatientId patientId, DoctorId doctorId, ConsultationType type, List<SlotId> slotIds);

    void cancelVisit(VisitId visitId);

    void completeVisit(VisitId visitId);
}