package pl.MiASI.patient.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.patient.application.domain.model.MedicalRecord;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryAdapterUnitTest {

    @Mock
    private SpringDataPatientRepository springDataRepository;

    @InjectMocks
    private PatientRepositoryAdapter repositoryAdapter;

    @Test
    @DisplayName("Should save new patient mapping correctly")
    void saveWhenNewPatientShouldMapAndSave() {
        // given
        UUID patientId = UUID.randomUUID();
        Patient patient = Patient.create(new PatientId(patientId), "Jan", "Kowalski", "12345", "111", "a@a.com");
        when(springDataRepository.findById(patientId)).thenReturn(Optional.empty());

        // when
        repositoryAdapter.save(patient);

        // then
        ArgumentCaptor<PatientJpaEntity> captor = ArgumentCaptor.forClass(PatientJpaEntity.class);
        verify(springDataRepository).save(captor.capture());
        PatientJpaEntity saved = captor.getValue();

        assertEquals(patientId, saved.getId());
        assertEquals("Jan", saved.getFirstName());
        assertEquals("Kowalski", saved.getLastName());
        assertEquals("12345", saved.getPesel());
        assertEquals("111", saved.getPhone());
        assertEquals("a@a.com", saved.getEmail());
        assertTrue(saved.getRecords().isEmpty());
    }

    @Test
    @DisplayName("Should save existing patient mapping correctly and clearing old records to add new ones")
    void saveWhenExistingPatientShouldMapAndSave() {
        // given
        UUID patientId = UUID.randomUUID();
        Patient patient = Patient.create(new PatientId(patientId), "Jan", "Kowalski", "12345", "111", "a@a.com");
        MedicalRecord record = new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), new DoctorId(UUID.randomUUID()), "d", "s", "p", "n", "t", LocalDateTime.now(), null, null);
        patient.addMedicalRecord(record);

        PatientJpaEntity existingEntity = new PatientJpaEntity();
        existingEntity.setId(patientId);
        existingEntity.setRecords(new ArrayList<>());
        
        when(springDataRepository.findById(patientId)).thenReturn(Optional.of(existingEntity));

        // when
        repositoryAdapter.save(patient);

        // then
        ArgumentCaptor<PatientJpaEntity> captor = ArgumentCaptor.forClass(PatientJpaEntity.class);
        verify(springDataRepository).save(captor.capture());
        PatientJpaEntity saved = captor.getValue();

        assertEquals(patientId, saved.getId());
        assertEquals(1, saved.getRecords().size());
        MedicalRecordJpaEntity savedRecord = saved.getRecords().get(0);
        assertEquals(record.getRecordId(), savedRecord.getId());
        assertEquals(record.getVisitId(), savedRecord.getVisitId());
        assertEquals(record.getDoctorId().value(), savedRecord.getDoctorId());
        assertEquals(record.getDiagnoses(), savedRecord.getDiagnoses());
    }

    @Test
    @DisplayName("Should find patient by id and map to domain correctly")
    void findByIdWhenExistsShouldReturnDomainObject() {
        // given
        UUID patientId = UUID.randomUUID();
        PatientJpaEntity entity = new PatientJpaEntity();
        entity.setId(patientId);
        entity.setFirstName("A");
        entity.setLastName("B");
        entity.setPesel("123");
        
        MedicalRecordJpaEntity recordEntity = new MedicalRecordJpaEntity();
        recordEntity.setId(UUID.randomUUID());
        recordEntity.setVisitId(UUID.randomUUID());
        recordEntity.setDoctorId(UUID.randomUUID());
        recordEntity.setDiagnoses("diag");
        entity.setRecords(List.of(recordEntity));
        
        when(springDataRepository.findById(patientId)).thenReturn(Optional.of(entity));

        // when
        Optional<Patient> result = repositoryAdapter.findById(new PatientId(patientId));

        // then
        assertTrue(result.isPresent());
        Patient patient = result.get();
        assertEquals(patientId, patient.getId().value());
        assertEquals("A", patient.getFirstName());
        assertEquals("B", patient.getLastName());
        assertEquals("123", patient.getPesel());
        assertEquals(1, patient.getMedicalRecords().size());
        assertEquals(recordEntity.getId(), patient.getMedicalRecords().get(0).getRecordId());
    }

    @Test
    @DisplayName("Should return empty optional when patient not found")
    void findByIdWhenNotExistsShouldReturnEmpty() {
        // given
        when(springDataRepository.findById(any())).thenReturn(Optional.empty());

        // when
        Optional<Patient> result = repositoryAdapter.findById(new PatientId(UUID.randomUUID()));

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find all patients and map them to domain")
    void findAllShouldReturnList() {
        // given
        PatientJpaEntity entity = new PatientJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setFirstName("A");
        entity.setRecords(List.of());
        when(springDataRepository.findAll()).thenReturn(List.of(entity));

        // when
        List<Patient> result = repositoryAdapter.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId().value());
    }

    @Test
    @DisplayName("Should return true if exists by pesel")
    void existsByPeselWhenExistsShouldReturnTrue() {
        // given
        when(springDataRepository.existsByPesel("123")).thenReturn(true);

        // when
        boolean exists = repositoryAdapter.existsByPesel("123");

        // then
        assertTrue(exists);
    }
}
