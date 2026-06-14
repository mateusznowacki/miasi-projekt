package pl.MiASI.patient.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordJpaEntityUnitTest {

    @Test
    @DisplayName("Should correctly set and get all fields in MedicalRecordJpaEntity")
    void gettersAndSettersWhenInvokedShouldWorkProperly() {
        // given
        MedicalRecordJpaEntity entity = new MedicalRecordJpaEntity();
        UUID id = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        UUID updatedBy = UUID.randomUUID();

        // when
        entity.setId(id);
        entity.setVisitId(visitId);
        entity.setDoctorId(doctorId);
        entity.setDiagnoses("Diag");
        entity.setSymptoms("Symp");
        entity.setPrescriptions("Presc");
        entity.setNotes("Notes");
        entity.setTestResults("Results");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(updatedBy);

        // then
        assertEquals(id, entity.getId());
        assertEquals(visitId, entity.getVisitId());
        assertEquals(doctorId, entity.getDoctorId());
        assertEquals("Diag", entity.getDiagnoses());
        assertEquals("Symp", entity.getSymptoms());
        assertEquals("Presc", entity.getPrescriptions());
        assertEquals("Notes", entity.getNotes());
        assertEquals("Results", entity.getTestResults());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
        assertEquals(updatedBy, entity.getUpdatedBy());
    }
}
