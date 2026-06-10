package pl.MiASI.medicalcare.domain.model;

import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Visit {
    private final VisitId visitId;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final ConsultationType consultationType;
    private VisitStatus status;
    private final List<SlotId> slotIds;

    public Visit(VisitId visitId, PatientId patientId, DoctorId doctorId, ConsultationType consultationType, VisitStatus status, List<SlotId> slotIds) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.consultationType = consultationType;
        this.status = status;
        this.slotIds = new ArrayList<>(slotIds);
    }

    public static Visit reserve(PatientId patientId, DoctorId doctorId, ConsultationType consultationType, List<SlotId> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            throw new IllegalArgumentException("Visit must have at least one slot");
        }
        return new Visit(new VisitId(), patientId, doctorId, consultationType, VisitStatus.RESERVED, slotIds);
    }

    public void cancel() {
        if (this.status != VisitStatus.RESERVED) {
            throw new IllegalStateException("Only reserved visits can be canceled");
        }
        this.status = VisitStatus.CANCELED;
    }

    public void complete() {
        if (this.status != VisitStatus.RESERVED) {
            throw new IllegalStateException("Only reserved visits can be completed");
        }
        this.status = VisitStatus.COMPLETED;
    }
}