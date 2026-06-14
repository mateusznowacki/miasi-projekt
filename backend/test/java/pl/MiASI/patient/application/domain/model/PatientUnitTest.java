package pl.MiASI.patient.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientUnitTest {

    @Test
    @DisplayName("Should create new Patient successfully")
    void createWhenValidDataShouldReturnPatient() {
        // given
        PatientId id = new PatientId(UUID.randomUUID());
        // when
        Patient patient = Patient.create(id, "Jan", "Kowalski", "12345678901", "123456789", "jan@example.com");
        // then
        assertEquals(id, patient.getId());
        assertEquals("Jan", patient.getFirstName());
        assertEquals("Kowalski", patient.getLastName());
        assertEquals("12345678901", patient.getPesel());
        assertEquals("123456789", patient.getPhone());
        assertEquals("jan@example.com", patient.getEmail());
        assertNull(patient.getAddress());
        assertTrue(patient.getMedicalRecords().isEmpty());
    }

    @Test
    @DisplayName("Should update personal data correctly")
    void updatePersonalDataWhenNewDataProvidedShouldUpdateFields() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "Kowalski", "123", "111", "a@b.com");
        // when
        patient.updatePersonalData("Anna", "Nowak", "222", "b@b.com", "Warszawa");
        // then
        assertEquals("Anna", patient.getFirstName());
        assertEquals("Nowak", patient.getLastName());
        assertEquals("222", patient.getPhone());
        assertEquals("b@b.com", patient.getEmail());
        assertEquals("Warszawa", patient.getAddress());
    }

    @Test
    @DisplayName("Should add medical record to patient's list")
    void addMedicalRecordWhenValidRecordShouldIncreaseListSize() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "K", "1", "1", "a");
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), new DoctorId(UUID.randomUUID()), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        // when
        patient.addMedicalRecord(record);
        // then
        assertEquals(1, patient.getMedicalRecords().size());
        assertTrue(patient.getMedicalRecords().contains(record));
    }

    @Test
    @DisplayName("Should update existing medical record")
    void updateMedicalRecordWhenRecordExistsShouldUpdateItsFields() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "K", "1", "1", "a");
        UUID recordId = UUID.randomUUID();
        DoctorId docId = new DoctorId(UUID.randomUUID());
        MedicalRecord record = new MedicalRecord(recordId, UUID.randomUUID(), docId, "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        patient.addMedicalRecord(record);
        DoctorId updatedBy = new DoctorId(UUID.randomUUID());
        // when
        patient.updateMedicalRecord(recordId, "new diag", "new symp", "new presc", "new notes", "new test", updatedBy);
        // then
        assertEquals("new diag", record.getDiagnoses());
        assertEquals("new symp", record.getSymptoms());
        assertEquals("new presc", record.getPrescriptions());
        assertEquals("new notes", record.getNotes());
        assertEquals("new test", record.getTestResults());
        assertEquals(updatedBy, record.getUpdatedBy());
        assertNotNull(record.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent medical record")
    void updateMedicalRecordWhenRecordNotFoundShouldThrowException() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "K", "1", "1", "a");
        DoctorId docId = new DoctorId(UUID.randomUUID());
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> patient.updateMedicalRecord(UUID.randomUUID(), "a", "b", "c", "d", "e", docId));
        assertEquals("Medical record not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return medical record by visit ID")
    void getMedicalRecordByVisitIdWhenExistsShouldReturnRecord() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "K", "1", "1", "a");
        UUID visitId = UUID.randomUUID();
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), visitId, new DoctorId(UUID.randomUUID()), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        patient.addMedicalRecord(record);
        // when
        var result = patient.getMedicalRecordByVisitId(visitId);
        // then
        assertTrue(result.isPresent());
        assertEquals(visitId, result.get().getVisitId());
    }

    @Test
    @DisplayName("Should return empty optional when medical record not found by visit ID")
    void getMedicalRecordByVisitIdWhenNotExistsShouldReturnEmpty() {
        // given
        Patient patient = Patient.create(new PatientId(UUID.randomUUID()), "Jan", "K", "1", "1", "a");
        // when
        var result = patient.getMedicalRecordByVisitId(UUID.randomUUID());
        // then
        assertTrue(result.isEmpty());
    }
}
