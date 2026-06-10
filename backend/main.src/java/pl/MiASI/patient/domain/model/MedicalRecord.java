package pl.MiASI.patient.domain.model;
import lombok.Getter;
import pl.MiASI.shared.domain.model.DoctorId;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
public class MedicalRecord {
    private final UUID recordId;
    private final UUID visitId;
    private final DoctorId doctorId;
    private String diagnoses;
    private String symptoms;
    private String prescriptions;
    private String notes;
    private final LocalDateTime createdAt;

    public MedicalRecord(UUID recordId, UUID visitId, DoctorId doctorId, String diagnoses, String symptoms, String prescriptions, String notes, LocalDateTime createdAt) {
        this.recordId = recordId; this.visitId = visitId; this.doctorId = doctorId; this.diagnoses = diagnoses;
        this.symptoms = symptoms; this.prescriptions = prescriptions; this.notes = notes; this.createdAt = createdAt;
    }

    public void update(String diagnoses, String symptoms, String prescriptions, String notes) {
        if (diagnoses != null) this.diagnoses = diagnoses;
        if (symptoms != null) this.symptoms = symptoms;
        if (prescriptions != null) this.prescriptions = prescriptions;
        if (notes != null) this.notes = notes;
    }
}