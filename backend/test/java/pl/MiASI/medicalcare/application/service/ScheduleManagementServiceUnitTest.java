package pl.MiASI.medicalcare.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.MiASI.medicalcare.application.domain.event.SlotFreedEvent;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.TimeRange;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleManagementServiceUnitTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ScheduleManagementService service;

    private DoctorId mockDoctorId() {
        return mock(DoctorId.class);
    }

    private TimeRange createTimeRange(int startHour, int endHour) {
        return new TimeRange(
                LocalDateTime.of(2023, 10, 10, startHour, 0),
                LocalDateTime.of(2023, 10, 10, endHour, 0)
        );
    }

    @Test
    @DisplayName("Should add time slots when schedule exists")
    void addTimeSlotsWhenScheduleExistsShouldUpdateAndSave() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule existingSchedule = Schedule.create(doctorId);
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(existingSchedule));

        List<AddSlotCommand> commands = List.of(new AddSlotCommand(createTimeRange(10, 11), "Room A"));

        // when
        service.addTimeSlots(doctorId, commands);

        // then
        verify(scheduleRepository).save(existingSchedule);
        assertThat(existingSchedule.slots()).hasSize(1);
    }

    @Test
    @DisplayName("Should add time slots creating new schedule when schedule does not exist")
    void addTimeSlotsWhenScheduleDoesNotExistShouldCreateNewAndSave() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        List<AddSlotCommand> commands = List.of(new AddSlotCommand(createTimeRange(10, 11), "Room A"));

        // when
        service.addTimeSlots(doctorId, commands);

        // then
        ArgumentCaptor<Schedule> scheduleCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleRepository).save(scheduleCaptor.capture());
        Schedule savedSchedule = scheduleCaptor.getValue();
        assertThat(savedSchedule.doctorId()).isEqualTo(doctorId);
        assertThat(savedSchedule.slots()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw exception when adding overlapping slots")
    void addTimeSlotsWhenOverlappingShouldThrowExceptionAndNotSave() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule existingSchedule = Schedule.create(doctorId);
        existingSchedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 12), "Room A")));
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(existingSchedule));

        List<AddSlotCommand> overlappingCommands = List.of(new AddSlotCommand(createTimeRange(11, 13), "Room B"));

        // when & then
        assertThatThrownBy(() -> service.addTimeSlots(doctorId, overlappingCommands))
                .isInstanceOf(IllegalArgumentException.class);
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should free slots and publish event when schedule exists")
    void freeSlotsWhenScheduleExistsShouldFreeAndPublishEvent() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule existingSchedule = Schedule.create(doctorId);
        existingSchedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room A")));
        SlotId slotId = existingSchedule.slots().get(0).getSlotId();
        existingSchedule.reserveSlots(List.of(slotId));
        
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(existingSchedule));

        // when
        service.freeSlots(doctorId, List.of(slotId));

        // then
        verify(scheduleRepository).save(existingSchedule);
        ArgumentCaptor<SlotFreedEvent> eventCaptor = ArgumentCaptor.forClass(SlotFreedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().slotIds()).contains(slotId);
        assertThat(existingSchedule.slots().get(0).getStatus().name()).isEqualTo("AVAILABLE");
    }

    @Test
    @DisplayName("Should throw exception when freeing slots and schedule not found")
    void freeSlotsWhenScheduleNotFoundShouldThrowException() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.freeSlots(doctorId, List.of(new SlotId())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule not found");
    }

    @Test
    @DisplayName("Should update slot when schedule exists")
    void updateSlotWhenScheduleExistsShouldUpdateAndSave() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule existingSchedule = Schedule.create(doctorId);
        existingSchedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room A")));
        SlotId slotId = existingSchedule.slots().get(0).getSlotId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(existingSchedule));

        TimeRange newTimeRange = createTimeRange(12, 13);
        
        // when
        service.updateSlot(doctorId, slotId, newTimeRange, "Room B");

        // then
        verify(scheduleRepository).save(existingSchedule);
        assertThat(existingSchedule.slots().get(0).getOffice()).isEqualTo("Room B");
        assertThat(existingSchedule.slots().get(0).getTimeRange()).isEqualTo(newTimeRange);
    }

    @Test
    @DisplayName("Should throw exception when updating slot and schedule not found")
    void updateSlotWhenScheduleNotFoundShouldThrowException() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.updateSlot(doctorId, new SlotId(), createTimeRange(10, 11), "Room B"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule not found");
    }

    @Test
    @DisplayName("Should remove slot when schedule exists")
    void removeSlotWhenScheduleExistsShouldRemoveAndSave() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule existingSchedule = Schedule.create(doctorId);
        existingSchedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room A")));
        SlotId slotId = existingSchedule.slots().get(0).getSlotId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(existingSchedule));

        // when
        service.removeSlot(doctorId, slotId);

        // then
        verify(scheduleRepository).save(existingSchedule);
        assertThat(existingSchedule.slots()).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when removing slot and schedule not found")
    void removeSlotWhenScheduleNotFoundShouldThrowException() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.removeSlot(doctorId, new SlotId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule not found");
    }
}
