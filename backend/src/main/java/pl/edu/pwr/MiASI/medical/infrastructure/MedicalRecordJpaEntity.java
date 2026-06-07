package pl.edu.pwr.MiASI.medical.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "medical_records", schema = "medical")
public class MedicalRecordJpaEntity {
    @Id
    private UUID id;
    private UUID wizytaId;
    private UUID patientId;
    private UUID lekarzId;
    private String kodICD10;
    private String diagnosisDescription;
    private String symptomDescription;
    private String prescriptionMedication;
    private String prescriptionDosage;
    private String notes;

    protected MedicalRecordJpaEntity() {}

    public MedicalRecordJpaEntity(UUID id, UUID wizytaId, UUID patientId, UUID lekarzId, String kodICD10, String diagnosisDescription, String symptomDescription, String prescriptionMedication, String prescriptionDosage, String notes) {
        this.id = id;
        this.wizytaId = wizytaId;
        this.patientId = patientId;
        this.lekarzId = lekarzId;
        this.kodICD10 = kodICD10;
        this.diagnosisDescription = diagnosisDescription;
        this.symptomDescription = symptomDescription;
        this.prescriptionMedication = prescriptionMedication;
        this.prescriptionDosage = prescriptionDosage;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getWizytaId() { return wizytaId; }
    public UUID getPacjentId() { return patientId; }
    public UUID getLekarzId() { return lekarzId; }
    public String getKodICD10() { return kodICD10; }
    public String getDiagnozaOpis() { return diagnosisDescription; }
    public String getObjawOpis() { return symptomDescription; }
    public String getReceptaLek() { return prescriptionMedication; }
    public String getReceptaDawkowanie() { return prescriptionDosage; }
    public String getNotatki() { return notes; }
}
