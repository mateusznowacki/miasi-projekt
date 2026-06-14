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
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerUnitTest {

    @Mock
    private ScheduleManagementUseCase scheduleManagementUseCase;

    @Mock
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @InjectMocks
    private ScheduleController controller;

    private TimeRange createTimeRange(int day, int hour) {
        return new TimeRange(
                LocalDateTime.of(2023, 10, day, hour, 0),
                LocalDateTime.of(2023, 10, day, hour + 1, 0)
        );
    }

    @Test
    @DisplayName("Should add time slots and return 200 OK")
    void addTimeSlotsWhenValidRequestShouldReturnOk() {
        // given
        UUID doctorId = UUID.randomUUID();
        AddSlotsRequest request = new AddSlotsRequest(List.of(new AddSlotCommand(createTimeRange(10, 10), "Room A")));

        // when
        ResponseEntity<Void> response = controller.addTimeSlots(doctorId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(scheduleManagementUseCase).addTimeSlots(eq(new DoctorId(doctorId)), eq(request.commands()));
    }

    @Test
    @DisplayName("Should update slot and return 200 OK")
    void updateSlotWhenValidRequestShouldReturnOk() {
        // given
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        UpdateSlotRequest request = new UpdateSlotRequest(createTimeRange(10, 10), "Room B");

        // when
        ResponseEntity<Void> response = controller.updateSlot(doctorId, slotId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(scheduleManagementUseCase).updateSlot(eq(new DoctorId(doctorId)), any(), eq(request.timeRange()), eq(request.office()));
    }

    @Test
    @DisplayName("Should remove slot and return 200 OK")
    void removeSlotWhenValidRequestShouldReturnOk() {
        // given
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();

        // when
        ResponseEntity<Void> response = controller.removeSlot(doctorId, slotId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(scheduleManagementUseCase).removeSlot(eq(new DoctorId(doctorId)), any());
    }

    @Test
    @DisplayName("Should get schedule by doctor and return 200 OK with data")
    void getScheduleByDoctorWhenScheduleExistsShouldReturnOkWithData() {
        // given
        UUID doctorId = UUID.randomUUID();
        Schedule schedule = Schedule.create(new DoctorId(doctorId));
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.of(schedule));

        // when
        ResponseEntity<ScheduleDto> response = controller.getScheduleByDoctor(doctorId, null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().doctorId()).isEqualTo(doctorId);
    }

    @Test
    @DisplayName("Should filter schedule slots by exact date")
    void getScheduleByDoctorWhenFilteredByDateShouldReturnMatchingSlots() {
        // given
        UUID doctorId = UUID.randomUUID();
        Schedule schedule = Schedule.create(new DoctorId(doctorId));
        schedule.addTimeSlots(List.of(
                new AddSlotCommand(createTimeRange(10, 10), "Room A"), // Oct 10
                new AddSlotCommand(createTimeRange(11, 10), "Room A")  // Oct 11
        ));
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.of(schedule));

        // when
        ResponseEntity<ScheduleDto> response = controller.getScheduleByDoctor(doctorId, "2023-10-10", null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().slots()).hasSize(1);
        assertThat(response.getBody().slots().get(0).timeRange().startTime().toLocalDate().toString()).isEqualTo("2023-10-10");
    }

    @Test
    @DisplayName("Should filter schedule slots by date range")
    void getScheduleByDoctorWhenFilteredByDateRangeShouldReturnMatchingSlots() {
        // given
        UUID doctorId = UUID.randomUUID();
        Schedule schedule = Schedule.create(new DoctorId(doctorId));
        schedule.addTimeSlots(List.of(
                new AddSlotCommand(createTimeRange(9, 10), "Room A"),  // Oct 9
                new AddSlotCommand(createTimeRange(10, 10), "Room A"), // Oct 10
                new AddSlotCommand(createTimeRange(12, 10), "Room A")  // Oct 12
        ));
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.of(schedule));

        // when
        ResponseEntity<ScheduleDto> response = controller.getScheduleByDoctor(doctorId, null, "2023-10-10", "2023-10-11", null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().slots()).hasSize(1);
        assertThat(response.getBody().slots().get(0).timeRange().startTime().toLocalDate().toString()).isEqualTo("2023-10-10");
    }

    @Test
    @DisplayName("Should filter schedule slots by status")
    void getScheduleByDoctorWhenFilteredByStatusShouldReturnMatchingSlots() {
        // given
        UUID doctorId = UUID.randomUUID();
        Schedule schedule = Schedule.create(new DoctorId(doctorId));
        schedule.addTimeSlots(List.of(
                new AddSlotCommand(createTimeRange(10, 10), "Room A"),
                new AddSlotCommand(createTimeRange(10, 11), "Room A")
        ));
        schedule.reserveSlots(List.of(schedule.slots().get(0).getSlotId())); // One is BOOKED
        
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.of(schedule));

        // when
        ResponseEntity<ScheduleDto> response = controller.getScheduleByDoctor(doctorId, null, null, null, "BOOKED");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().slots()).hasSize(1);
        assertThat(response.getBody().slots().get(0).status()).isEqualTo("BOOKED");
    }

    @Test
    @DisplayName("Should get schedule by doctor and return 404 NotFound when schedule does not exist")
    void getScheduleByDoctorWhenScheduleDoesNotExistShouldReturnNotFound() {
        // given
        UUID doctorId = UUID.randomUUID();
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.empty());

        // when
        ResponseEntity<ScheduleDto> response = controller.getScheduleByDoctor(doctorId, null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
