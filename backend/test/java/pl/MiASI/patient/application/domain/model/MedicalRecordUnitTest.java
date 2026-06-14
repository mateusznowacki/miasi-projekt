package pl.MiASI.patient.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordUnitTest {

    @Test
    @DisplayName("Should create medical record with all fields")
    void constructorWhenAllFieldsProvidedShouldSetThem() {
        // given
        UUID recordId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();
        DoctorId docId = new DoctorId(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        // when
        MedicalRecord record = new MedicalRecord(recordId, visitId, docId, "diag", "symp", "presc", "notes", "test", now, null, null);
        // then
        assertEquals(recordId, record.getRecordId());
        assertEquals(visitId, record.getVisitId());
        assertEquals(docId, record.getDoctorId());
        assertEquals("diag", record.getDiagnoses());
        assertEquals(now, record.getCreatedAt());
        assertNull(record.getUpdatedAt());
        assertNull(record.getUpdatedBy());
    }

    @Test
    @DisplayName("Should update fields and set updated time and user")
    void updateWhenNewDataProvidedShouldChangeFieldsAndSetUpdated() {
        // given
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), new DoctorId(UUID.randomUUID()), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        DoctorId updatedBy = new DoctorId(UUID.randomUUID());
        
        // when
        record.update("newDiag", "newSymp", "newPresc", "newNotes", "newTest", updatedBy);
        
        // then
        assertEquals("newDiag", record.getDiagnoses());
        assertEquals("newSymp", record.getSymptoms());
        assertEquals("newPresc", record.getPrescriptions());
        assertEquals("newNotes", record.getNotes());
        assertEquals("newTest", record.getTestResults());
        assertEquals(updatedBy, record.getUpdatedBy());
        assertNotNull(record.getUpdatedAt());
    }

    @Test
    @DisplayName("Should not overwrite fields if null is provided in update")
    void updateWhenNullsProvidedShouldNotOverwriteFields() {
        // given
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), new DoctorId(UUID.randomUUID()), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        DoctorId updatedBy = new DoctorId(UUID.randomUUID());
        
        // when
        record.update(null, null, null, null, null, updatedBy);
        
        // then
        assertEquals("diag", record.getDiagnoses());
        assertEquals("symp", record.getSymptoms());
        assertEquals("presc", record.getPrescriptions());
        assertEquals("notes", record.getNotes());
        assertEquals("test", record.getTestResults());
        assertEquals(updatedBy, record.getUpdatedBy());
        assertNotNull(record.getUpdatedAt());
    }
}
