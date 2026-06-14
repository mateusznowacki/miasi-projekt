package pl.MiASI.patient.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientJpaEntityUnitTest {

    @Test
    @DisplayName("Should correctly set and get all fields in PatientJpaEntity")
    void gettersAndSettersWhenInvokedShouldWorkProperly() {
        // given
        PatientJpaEntity entity = new PatientJpaEntity();
        UUID id = UUID.randomUUID();
        
        MedicalRecordJpaEntity record = new MedicalRecordJpaEntity();
        record.setId(UUID.randomUUID());
        
        // when
        entity.setId(id);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setPesel("12345678901");
        entity.setPhone("123456789");
        entity.setEmail("john@doe.com");
        entity.setAddress("Street 1");
        entity.setRecords(List.of(record));
        
        // then
        assertEquals(id, entity.getId());
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals("12345678901", entity.getPesel());
        assertEquals("123456789", entity.getPhone());
        assertEquals("john@doe.com", entity.getEmail());
        assertEquals("Street 1", entity.getAddress());
        assertEquals(1, entity.getRecords().size());
        assertEquals(record.getId(), entity.getRecords().get(0).getId());
    }
}
