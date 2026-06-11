package pl.MiASI.patient.application.port.in;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.shared.domain.model.DoctorId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface PatientUseCase {
    PatientId registerPatient(String firstName, String lastName, String pesel, String phone, String email, String password);
    void updatePersonalData(PatientId id, String firstName, String lastName, String phone, String email, String address);
    void addMedicalRecord(PatientId id, UUID visitId, DoctorId doctorId, String diagnoses, String symptoms, String prescriptions, String notes, String testResults);
    void updateMedicalRecord(PatientId id, UUID recordId, String diagnoses, String symptoms, String prescriptions, String notes, String testResults, DoctorId updatedBy);
    Optional<Patient> getPatientProfile(PatientId id);
    Optional<pl.MiASI.patient.domain.model.MedicalRecord> getMedicalRecordByVisitId(PatientId id, UUID visitId);
    List<Patient> listPatients();
    List<Patient> searchPatients(String firstName, String lastName, String pesel, String patientCardNumber);
}