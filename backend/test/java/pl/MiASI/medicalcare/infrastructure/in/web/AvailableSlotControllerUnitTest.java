package pl.MiASI.medicalcare.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.TimeRange;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailableSlotControllerUnitTest {

    @Mock
    private StaffUseCase staffUseCase;

    @Mock
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @InjectMocks
    private AvailableSlotController controller;

    @Test
    @DisplayName("Should return available slots for all doctors")
    void getAvailableSlotsWhenNoFiltersShouldReturnAllAvailable() {
        // given
        UUID doctorUuid = UUID.randomUUID();
        StaffMember doctor = StaffMember.create(doctorUuid, StaffRole.DOCTOR, "John", "Doe", "john.doe@test.com", "Cardiology", "123456789", "dep", "Dr", "sch");
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doctor));

        Schedule schedule = Schedule.create(new DoctorId(doctorUuid));
        schedule.addTimeSlots(List.of(new AddSlotCommand(
                new TimeRange(LocalDateTime.of(2023, 10, 10, 10, 0), LocalDateTime.of(2023, 10, 10, 11, 0)),
                "Room A"
        )));
        when(scheduleQueryUseCase.getAllSchedules()).thenReturn(List.of(schedule));

        // when
        ResponseEntity<List<AvailableSlotDto>> response = controller.getAvailableSlots(null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        AvailableSlotDto dto = response.getBody().get(0);
        assertThat(dto.doctorId()).isEqualTo(doctorUuid);
        assertThat(dto.office()).isEqualTo("Room A");
        assertThat(dto.specialization()).isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Should filter out non-available slots")
    void getAvailableSlotsWhenSlotsAreBookedShouldFilterThemOut() {
        // given
        UUID doctorUuid = UUID.randomUUID();
        StaffMember doctor = StaffMember.create(doctorUuid, StaffRole.DOCTOR, "John", "Doe", "john.doe@test.com", "Cardiology", "123456789", "dep", "Dr", "sch");
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doctor));

        Schedule schedule = Schedule.create(new DoctorId(doctorUuid));
        schedule.addTimeSlots(List.of(new AddSlotCommand(
                new TimeRange(LocalDateTime.of(2023, 10, 10, 10, 0), LocalDateTime.of(2023, 10, 10, 11, 0)),
                "Room A"
        )));
        schedule.reserveSlots(List.of(schedule.slots().get(0).getSlotId())); // Book it
        
        when(scheduleQueryUseCase.getAllSchedules()).thenReturn(List.of(schedule));

        // when
        ResponseEntity<List<AvailableSlotDto>> response = controller.getAvailableSlots(null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
