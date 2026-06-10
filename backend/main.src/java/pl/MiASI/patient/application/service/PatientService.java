package pl.MiASI.patient.application.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.patient.domain.model.MedicalRecord;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.patient.domain.repository.PatientRepository;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.domain.model.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService implements PatientUseCase {
    private final PatientRepository patientRepository;
    private final AuthUseCase authUseCase;

    @Override
    @Transactional
    public PatientId registerPatient(String firstName, String lastName, String pesel, String phone, String email, String password) {
        // Zapis do IAM
        UUID accountId = authUseCase.registerUser(email, password, Role.PATIENT).value();
        PatientId patientId = new PatientId(accountId);
        
        // Zapis profilu w Kontekście Pacjenta
        Patient patient = Patient.create(patientId, firstName, lastName, pesel, phone, email);
        patientRepository.save(patient);
        return patientId;
    }

    @Override
    @Transactional
    public void updatePersonalData(PatientId id, String firstName, String lastName, String phone, String email) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.updatePersonalData(firstName, lastName, phone, email);
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void addMedicalRecord(PatientId id, UUID visitId, DoctorId doctorId, String diagnoses, String symptoms, String prescriptions, String notes) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.addMedicalRecord(new MedicalRecord(UUID.randomUUID(), visitId, doctorId, diagnoses, symptoms, prescriptions, notes, LocalDateTime.now()));
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void updateMedicalRecord(PatientId id, UUID recordId, String diagnoses, String symptoms, String prescriptions, String notes) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.updateMedicalRecord(recordId, diagnoses, symptoms, prescriptions, notes);
        patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> getMedicalRecordByVisitId(PatientId id, UUID visitId) {
        return patientRepository.findById(id).flatMap(p -> p.getMedicalRecordByVisitId(visitId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientProfile(PatientId id) {
        return patientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> listPatients() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String query) {
        if (query == null || query.isBlank()) return listPatients();
        String lowerQuery = query.toLowerCase();
        return patientRepository.findAll().stream()
                .filter(p -> p.getFirstName().toLowerCase().contains(lowerQuery) || 
                             p.getLastName().toLowerCase().contains(lowerQuery) || 
                             p.getPesel().contains(query))
                .toList();
    }
}