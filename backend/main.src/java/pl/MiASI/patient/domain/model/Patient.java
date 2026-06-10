package pl.MiASI.patient.domain.model;
import lombok.Getter;
import pl.MiASI.shared.domain.model.PatientId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
@Getter
public class Patient {
    private final PatientId id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phone;
    private String email;
    private final List<MedicalRecord> medicalRecords;

    public Patient(PatientId id, String firstName, String lastName, String pesel, String phone, String email, List<MedicalRecord> records) {
        this.id = id; this.firstName = firstName; this.lastName = lastName; this.pesel = pesel; this.phone = phone; this.email = email;
        this.medicalRecords = new ArrayList<>(records);
    }
    public static Patient create(PatientId id, String firstName, String lastName, String pesel, String phone, String email) {
        return new Patient(id, firstName, lastName, pesel, phone, email, new ArrayList<>());
    }
    public void updatePersonalData(String firstName, String lastName, String phone, String email) {
        this.firstName = firstName; this.lastName = lastName; this.phone = phone; this.email = email;
    }
    public void addMedicalRecord(MedicalRecord record) { this.medicalRecords.add(record); }
    
    public void updateMedicalRecord(UUID recordId, String diagnoses, String symptoms, String prescriptions, String notes) {
        MedicalRecord record = this.medicalRecords.stream()
                .filter(r -> r.getRecordId().equals(recordId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        record.update(diagnoses, symptoms, prescriptions, notes);
    }
    
    public Optional<MedicalRecord> getMedicalRecordByVisitId(UUID visitId) {
        return this.medicalRecords.stream()
                .filter(r -> r.getVisitId().equals(visitId))
                .findFirst();
    }
}