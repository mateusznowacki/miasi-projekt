package pl.MiASI.patient.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.patient.application.domain.model.MedicalRecord;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.patient.application.port.out.PatientRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.medicalcare.application.domain.event.RecordCreatedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceUnitTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthUseCase authUseCase;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private final UUID accountIdUuid = UUID.randomUUID();
    private final PatientId patientId = new PatientId(accountIdUuid);
    private final DoctorId doctorId = new DoctorId(UUID.randomUUID());
    private final UUID visitId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testPatient = Patient.create(patientId, "Jan", "Kowalski", "12345678901", "123456789", "jan@example.com");
    }

    @Test
    @DisplayName("Should successfully register patient and return its ID")
    void registerPatientWhenPeselNotTakenShouldSaveAndReturnId() {
        // given
        when(patientRepository.existsByPesel("12345678901")).thenReturn(false);
        when(authUseCase.registerUser("jan@example.com", "pass", Role.PATIENT)).thenReturn(new AccountId(accountIdUuid));

        // when
        PatientId result = patientService.registerPatient("Jan", "Kowalski", "12345678901", "123456789", "jan@example.com", "pass");

        // then
        assertNotNull(result);
        assertEquals(patientId, result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw exception when registering patient with taken PESEL")
    void registerPatientWhenPeselTakenShouldThrowException() {
        // given
        when(patientRepository.existsByPesel("12345678901")).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> patientService.registerPatient("Jan", "Kowalski", "12345678901", "123456789", "jan@example.com", "pass"));
        assertEquals("PESEL taken", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should successfully update patient's personal data")
    void updatePersonalDataWhenPatientFoundShouldUpdate() {
        // given
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // when
        patientService.updatePersonalData(patientId, "Janusz", "Nowak", "987654321", "janusz@example.com", "New Address");

        // then
        assertEquals("Janusz", testPatient.getFirstName());
        assertEquals("Nowak", testPatient.getLastName());
        assertEquals("987654321", testPatient.getPhone());
        assertEquals("janusz@example.com", testPatient.getEmail());
        assertEquals("New Address", testPatient.getAddress());
        verify(authUseCase).updateEmail(any(AccountId.class), eq("janusz@example.com"));
        verify(patientRepository).save(testPatient);
    }

    @Test
    @DisplayName("Should successfully add medical record and publish event")
    void addMedicalRecordWhenValidDataShouldAddRecord() {
        // given
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // when
        patientService.addMedicalRecord(patientId, visitId, doctorId, "Flu", "Fever", "Rest", "Notes", "Test results");

        // then
        assertEquals(1, testPatient.getMedicalRecords().size());
        verify(patientRepository).save(testPatient);
        verify(eventPublisher).publishEvent(any(RecordCreatedEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when adding medical record with missing data")
    void addMedicalRecordWhenMissingDataShouldThrowException() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> patientService.addMedicalRecord(patientId, visitId, doctorId, "", "Fever", "Rest", "Notes", "Test results"));
        assertEquals("Wyniki badania, rozpoznanie oraz zalecenia są wymagane.", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully update medical record")
    void updateMedicalRecordWhenValidDataShouldUpdateRecord() {
        // given
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), visitId, doctorId, "Flu", "Fever", "Rest", "Notes", "Test results", LocalDateTime.now(), null, null);
        testPatient.addMedicalRecord(record);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // when
        patientService.updateMedicalRecord(patientId, record.getRecordId(), "Covid", "Cough", "Meds", "More notes", "Positive", doctorId);

        // then
        assertEquals("Covid", record.getDiagnoses());
        assertEquals("Cough", record.getSymptoms());
        assertEquals("Meds", record.getPrescriptions());
        assertNotNull(record.getUpdatedAt());
        verify(patientRepository).save(testPatient);
    }

    @Test
    @DisplayName("Should throw exception when updating medical record with missing diagnoses")
    void updateMedicalRecordWhenMissingDiagnosesShouldThrowException() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> patientService.updateMedicalRecord(patientId, UUID.randomUUID(), "", "Cough", "Meds", "More notes", "Positive", doctorId));
        assertEquals("Rozpoznanie (kody ICD-10) jest wymagane.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return medical record when found by visit ID")
    void getMedicalRecordByVisitIdWhenExistsShouldReturnRecord() {
        // given
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), visitId, doctorId, "Flu", "Fever", "Rest", "Notes", "Test results", LocalDateTime.now(), null, null);
        testPatient.addMedicalRecord(record);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // when
        Optional<MedicalRecord> result = patientService.getMedicalRecordByVisitId(patientId, visitId);

        // then
        assertTrue(result.isPresent());
        assertEquals(visitId, result.get().getVisitId());
    }

    @Test
    @DisplayName("Should return patient profile when patient exists")
    void getPatientProfileWhenExistsShouldReturnPatient() {
        // given
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // when
        Optional<Patient> result = patientService.getPatientProfile(patientId);

        // then
        assertTrue(result.isPresent());
        assertEquals(testPatient, result.get());
    }

    @Test
    @DisplayName("Should return all patients")
    void listPatientsShouldReturnList() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.listPatients();

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }

    @Test
    @DisplayName("Should return patients matching first name only")
    void searchPatientsWhenFirstNameProvidedShouldReturnMatchingPatients() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients("Jan", null, null, null);

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }

    @Test
    @DisplayName("Should return patients matching last name fragment")
    void searchPatientsWhenLastNameFragmentProvidedShouldReturnMatchingPatients() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients(null, "Kowal", null, null);

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }

    @Test
    @DisplayName("Should return patients matching both first and last name")
    void searchPatientsWhenFirstAndLastNameProvidedShouldReturnMatchingPatients() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients("Jan", "Kowalski", null, null);

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when name does not match")
    void searchPatientsWhenNameDoesNotMatchShouldReturnEmpty() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients("Anna", "Nowak", null, null);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return patients matching PESEL fragment")
    void searchPatientsWhenPeselFragmentProvidedShouldReturnMatchingPatients() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients(null, null, "12345", null);

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when PESEL does not match")
    void searchPatientsWhenPeselDoesNotMatchShouldReturnEmpty() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients(null, null, "99999", null);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return patients matching exact Card Number")
    void searchPatientsWhenCardNumberProvidedShouldReturnMatchingPatients() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients(null, null, null, testPatient.getId().value().toString());

        // then
        assertEquals(1, result.size());
        assertEquals(testPatient, result.get(0));
    }
    
    @Test
    @DisplayName("Should return empty list when Card Number does not match")
    void searchPatientsWhenCardNumberDoesNotMatchShouldReturnEmpty() {
        // given
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        // when
        List<Patient> result = patientService.searchPatients(null, null, null, UUID.randomUUID().toString());

        // then
        assertTrue(result.isEmpty());
    }
}
