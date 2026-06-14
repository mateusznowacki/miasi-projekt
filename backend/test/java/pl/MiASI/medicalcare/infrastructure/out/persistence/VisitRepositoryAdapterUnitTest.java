package pl.MiASI.medicalcare.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.medicalcare.application.domain.model.ConsultationType;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.medicalcare.application.domain.model.VisitId;
import pl.MiASI.medicalcare.application.domain.model.VisitStatus;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitRepositoryAdapterUnitTest {

    @Mock
    private SpringDataVisitRepository repository;

    @InjectMocks
    private VisitRepositoryAdapter adapter;

    @Test
    @DisplayName("Should save visit mapping to JPA entity correctly")
    void saveWhenVisitIsValidShouldSaveMappedEntity() {
        // given
        PatientId patientId = new PatientId(UUID.randomUUID());
        DoctorId doctorId = new DoctorId(UUID.randomUUID());
        Visit visit = Visit.reserve(patientId, doctorId, ConsultationType.GENERAL, List.of(new SlotId()));

        // when
        adapter.save(visit);

        // then
        ArgumentCaptor<VisitJpaEntity> captor = ArgumentCaptor.forClass(VisitJpaEntity.class);
        verify(repository).save(captor.capture());
        
        VisitJpaEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo(visit.getVisitId().value());
        assertThat(entity.getPatientId()).isEqualTo(patientId.value());
        assertThat(entity.getDoctorId()).isEqualTo(doctorId.value());
        assertThat(entity.getConsultationType()).isEqualTo(ConsultationType.GENERAL);
        assertThat(entity.getStatus()).isEqualTo(VisitStatus.RESERVED);
    }

    @Test
    @DisplayName("Should find visit by ID and map to domain")
    void findByIdWhenExistsShouldReturnMappedDomain() {
        // given
        UUID visitIdUuid = UUID.randomUUID();
        VisitJpaEntity entity = new VisitJpaEntity();
        entity.setId(visitIdUuid);
        entity.setPatientId(UUID.randomUUID());
        entity.setDoctorId(UUID.randomUUID());
        entity.setConsultationType(ConsultationType.GENERAL);
        entity.setStatus(VisitStatus.RESERVED);
        entity.setSlotIds(List.of(UUID.randomUUID()));
        
        when(repository.findById(visitIdUuid)).thenReturn(Optional.of(entity));

        // when
        Optional<Visit> result = adapter.findById(new VisitId(visitIdUuid));

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getVisitId().value()).isEqualTo(visitIdUuid);
        assertThat(result.get().getPatientId().value()).isEqualTo(entity.getPatientId());
    }

    @Test
    @DisplayName("Should find visits by patient ID and map to domain")
    void findByPatientIdWhenExistsShouldReturnMappedDomainList() {
        // given
        UUID patientIdUuid = UUID.randomUUID();
        VisitJpaEntity entity = new VisitJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setPatientId(patientIdUuid);
        entity.setDoctorId(UUID.randomUUID());
        entity.setConsultationType(ConsultationType.GENERAL);
        entity.setStatus(VisitStatus.RESERVED);
        entity.setSlotIds(List.of(UUID.randomUUID()));
        
        when(repository.findByPatientId(patientIdUuid)).thenReturn(List.of(entity));

        // when
        List<Visit> result = adapter.findByPatientId(new PatientId(patientIdUuid));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId().value()).isEqualTo(patientIdUuid);
    }

    @Test
    @DisplayName("Should find visits by doctor ID and map to domain")
    void findByDoctorIdWhenExistsShouldReturnMappedDomainList() {
        // given
        UUID doctorIdUuid = UUID.randomUUID();
        VisitJpaEntity entity = new VisitJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setPatientId(UUID.randomUUID());
        entity.setDoctorId(doctorIdUuid);
        entity.setConsultationType(ConsultationType.GENERAL);
        entity.setStatus(VisitStatus.RESERVED);
        entity.setSlotIds(List.of(UUID.randomUUID()));
        
        when(repository.findByDoctorId(doctorIdUuid)).thenReturn(List.of(entity));

        // when
        List<Visit> result = adapter.findByDoctorId(new DoctorId(doctorIdUuid));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctorId().value()).isEqualTo(doctorIdUuid);
    }
}
