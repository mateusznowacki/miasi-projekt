package pl.MiASI.medicalcare.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceUnitTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleQueryService service;

    private DoctorId mockDoctorId() {
        return mock(DoctorId.class);
    }

    @Test
    @DisplayName("Should return schedule by doctor ID when it exists")
    void getScheduleByDoctorWhenExistsShouldReturnSchedule() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule schedule = Schedule.create(doctorId);
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(schedule));

        // when
        Optional<Schedule> result = service.getScheduleByDoctor(doctorId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(schedule);
    }

    @Test
    @DisplayName("Should return empty optional when schedule does not exist")
    void getScheduleByDoctorWhenDoesNotExistShouldReturnEmpty() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        // when
        Optional<Schedule> result = service.getScheduleByDoctor(doctorId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return all schedules")
    void getAllSchedulesShouldReturnList() {
        // given
        Schedule schedule1 = Schedule.create(mockDoctorId());
        Schedule schedule2 = Schedule.create(mockDoctorId());
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule1, schedule2));

        // when
        List<Schedule> result = service.getAllSchedules();

        // then
        assertThat(result).hasSize(2).containsExactly(schedule1, schedule2);
    }

    @Test
    @DisplayName("Should return empty list when no schedules exist")
    void getAllSchedulesWhenEmptyShouldReturnEmptyList() {
        // given
        when(scheduleRepository.findAll()).thenReturn(List.of());

        // when
        List<Schedule> result = service.getAllSchedules();

        // then
        assertThat(result).isEmpty();
    }
}
