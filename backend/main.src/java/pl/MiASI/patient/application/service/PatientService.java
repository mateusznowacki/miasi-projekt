package pl.MiASI.patient.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.patient.application.domain.model.MedicalRecord;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.patient.application.port.out.PatientRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService implements PatientUseCase {
    private final PatientRepository patientRepository;
    private final AuthUseCase authUseCase;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PatientId registerPatient(String firstName, String lastName, String pesel, String phone, String email, String password) {
        if (patientRepository.existsByPesel(pesel)) {
            throw new IllegalArgumentException("PESEL taken");
        }
        // Zapis do IAM
        UUID accountId = authUseCase.registerUser(email, password, Role.PATIENT).value();
        PatientId patientId = new PatientId(accountId);

        // Zapis profilu w Kontekście Pacjenta
        Patient patient = Patient.create(patientId, firstName, lastName, pesel, phone, email);
        patientRepository.save(patient);

        // Send activation email
        System.out.println("Sending activation link to " + email + ": /api/auth/activate?token=" + accountId);

        return patientId;
    }

    @Override
    @Transactional
    public void updatePersonalData(PatientId id, String firstName, String lastName, String phone, String email, String address) {
        authUseCase.updateEmail(new pl.MiASI.iam.application.domain.model.AccountId(id.value()), email);
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.updatePersonalData(firstName, lastName, phone, email, address);
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void addMedicalRecord(PatientId id, UUID visitId, DoctorId doctorId, String diagnoses, String symptoms, String prescriptions, String notes, String testResults) {
        if (diagnoses == null || diagnoses.isBlank() || symptoms == null || symptoms.isBlank() || prescriptions == null || prescriptions.isBlank()) {
            throw new IllegalArgumentException("Wyniki badania, rozpoznanie oraz zalecenia są wymagane.");
        }
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.addMedicalRecord(new MedicalRecord(UUID.randomUUID(), visitId, doctorId, diagnoses, symptoms, prescriptions, notes, testResults, LocalDateTime.now(), null, null));
        patientRepository.save(patient);
        eventPublisher.publishEvent(new pl.MiASI.medicalcare.application.domain.event.RecordCreatedEvent(new pl.MiASI.medicalcare.application.domain.model.VisitId(visitId)));
    }

    @Override
    @Transactional
    public void updateMedicalRecord(PatientId id, UUID recordId, String diagnoses, String symptoms, String prescriptions, String notes, String testResults, DoctorId updatedBy) {
        if (diagnoses == null || diagnoses.isBlank()) {
            throw new IllegalArgumentException("Rozpoznanie (kody ICD-10) jest wymagane.");
        }
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.updateMedicalRecord(recordId, diagnoses, symptoms, prescriptions, notes, testResults, updatedBy);
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
    public List<Patient> searchPatients(String firstName, String lastName, String pesel, String patientCardNumber) {
        return patientRepository.findAll().stream()
                .filter(p -> matchesName(p, firstName, lastName))
                .filter(p -> pesel == null || pesel.isBlank() || p.getPesel().contains(pesel))
                .filter(p -> patientCardNumber == null || patientCardNumber.isBlank() || p.getId().value().toString().equals(patientCardNumber))
                .toList();
    }

    private boolean matchesName(Patient patient, String firstName, String lastName) {
        boolean hasFirst = firstName != null && !firstName.isBlank();
        boolean hasLast = lastName != null && !lastName.isBlank();

        if (!hasFirst && !hasLast) {
            return true;
        }

        if (hasFirst && hasLast) {
            return containsIgnoreCase(patient.getFirstName(), firstName)
                    && containsIgnoreCase(patient.getLastName(), lastName);
        }

        String term = hasFirst ? firstName : lastName;
        String fullName = patient.getFirstName() + " " + patient.getLastName();
        return containsIgnoreCase(patient.getFirstName(), term)
                || containsIgnoreCase(patient.getLastName(), term)
                || containsIgnoreCase(fullName, term);
    }

    private boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && needle != null
                && haystack.toLowerCase().contains(needle.toLowerCase());
    }
}