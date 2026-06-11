package pl.MiASI.patient.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
public class MedicalRecordJpaEntity {
    @Id
    private UUID id;
    private UUID visitId;
    private UUID doctorId;
    private String diagnoses;
    private String symptoms;
    private String prescriptions;
    private String notes;
    private String testResults;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}