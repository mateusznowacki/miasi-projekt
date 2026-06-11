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
    private final LocalDateTime createdAt;
    private String diagnoses;
    private String symptoms;
    private String prescriptions;
    private String notes;
    private String testResults;
    private LocalDateTime updatedAt;
    private DoctorId updatedBy;

    public MedicalRecord(UUID recordId, UUID visitId, DoctorId doctorId, String diagnoses, String symptoms, String prescriptions, String notes, String testResults, LocalDateTime createdAt, LocalDateTime updatedAt, DoctorId updatedBy) {
        this.recordId = recordId;
        this.visitId = visitId;
        this.doctorId = doctorId;
        this.diagnoses = diagnoses;
        this.symptoms = symptoms;
        this.prescriptions = prescriptions;
        this.notes = notes;
        this.testResults = testResults;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void update(String diagnoses, String symptoms, String prescriptions, String notes, String testResults, DoctorId updatedBy) {
        if (diagnoses != null) this.diagnoses = diagnoses;
        if (symptoms != null) this.symptoms = symptoms;
        if (prescriptions != null) this.prescriptions = prescriptions;
        if (notes != null) this.notes = notes;
        if (testResults != null) this.testResults = testResults;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}