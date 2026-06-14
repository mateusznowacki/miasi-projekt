package pl.MiASI.medicalcare.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.medicalcare.application.domain.model.*;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleRepositoryAdapterUnitTest {

    @Mock
    private SpringDataScheduleRepository repository;

    @InjectMocks
    private ScheduleRepositoryAdapter adapter;

    @Test
    @DisplayName("Should save schedule mapping to JPA entity correctly")
    void saveWhenScheduleIsValidShouldSaveMappedEntity() {
        // given
        DoctorId doctorId = new DoctorId(UUID.randomUUID());
        Schedule schedule = Schedule.create(doctorId);
        
        when(repository.findById(schedule.scheduleId().value())).thenReturn(Optional.empty());

        // when
        adapter.save(schedule);

        // then
        ArgumentCaptor<ScheduleJpaEntity> captor = ArgumentCaptor.forClass(ScheduleJpaEntity.class);
        verify(repository).save(captor.capture());
        
        ScheduleJpaEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo(schedule.scheduleId().value());
        assertThat(entity.getDoctorId()).isEqualTo(doctorId.value());
    }

    @Test
    @DisplayName("Should find schedule by ID and map to domain")
    void findByIdWhenExistsShouldReturnMappedDomain() {
        // given
        UUID scheduleIdUuid = UUID.randomUUID();
        ScheduleJpaEntity entity = new ScheduleJpaEntity();
        entity.setId(scheduleIdUuid);
        entity.setDoctorId(UUID.randomUUID());
        entity.setSlots(new ArrayList<>());
        
        when(repository.findById(scheduleIdUuid)).thenReturn(Optional.of(entity));

        // when
        Optional<Schedule> result = adapter.findById(new ScheduleId(scheduleIdUuid));

        // then
        assertThat(result).isPresent();
        assertThat(result.get().scheduleId().value()).isEqualTo(scheduleIdUuid);
    }

    @Test
    @DisplayName("Should find schedule by Doctor ID and map to domain")
    void findByDoctorIdWhenExistsShouldReturnMappedDomain() {
        // given
        UUID doctorIdUuid = UUID.randomUUID();
        ScheduleJpaEntity entity = new ScheduleJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setDoctorId(doctorIdUuid);
        entity.setSlots(new ArrayList<>());
        
        when(repository.findByDoctorId(doctorIdUuid)).thenReturn(Optional.of(entity));

        // when
        Optional<Schedule> result = adapter.findByDoctorId(new DoctorId(doctorIdUuid));

        // then
        assertThat(result).isPresent();
        assertThat(result.get().doctorId().value()).isEqualTo(doctorIdUuid);
    }

    @Test
    @DisplayName("Should return all schedules mapped to domain")
    void findAllShouldReturnMappedList() {
        // given
        ScheduleJpaEntity entity1 = new ScheduleJpaEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setDoctorId(UUID.randomUUID());
        entity1.setSlots(new ArrayList<>());
        
        when(repository.findAll()).thenReturn(List.of(entity1));

        // when
        List<Schedule> result = adapter.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).scheduleId().value()).isEqualTo(entity1.getId());
    }
}
