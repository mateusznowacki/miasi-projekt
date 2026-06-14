package pl.MiASI.medicalcare.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ScheduleUnitTest {

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
    @DisplayName("Should create empty schedule")
    void createWhenValidDataShouldReturnEmptySchedule() {
        // given
        DoctorId doctorId = mockDoctorId();

        // when
        Schedule schedule = Schedule.create(doctorId);

        // then
        assertThat(schedule.scheduleId()).isNotNull();
        assertThat(schedule.doctorId()).isEqualTo(doctorId);
        assertThat(schedule.slots()).isEmpty();
    }

    @Test
    @DisplayName("Should add multiple non-overlapping time slots")
    void addTimeSlotsWhenNoOverlapShouldAddSlots() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        AddSlotCommand cmd1 = new AddSlotCommand(createTimeRange(10, 11), "Room 101");
        AddSlotCommand cmd2 = new AddSlotCommand(createTimeRange(11, 12), "Room 102");

        // when
        schedule.addTimeSlots(List.of(cmd1, cmd2));

        // then
        assertThat(schedule.slots()).hasSize(2);
        assertThat(schedule.slots().get(0).getTimeRange()).isEqualTo(cmd1.timeRange());
        assertThat(schedule.slots().get(1).getTimeRange()).isEqualTo(cmd2.timeRange());
    }

    @Test
    @DisplayName("Should throw exception when adding overlapping time slots")
    void addTimeSlotsWhenOverlapShouldThrowException() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        AddSlotCommand cmd1 = new AddSlotCommand(createTimeRange(10, 12), "Room 101");
        AddSlotCommand cmd2 = new AddSlotCommand(createTimeRange(11, 13), "Room 102");

        // when & then
        assertThatThrownBy(() -> schedule.addTimeSlots(List.of(cmd1, cmd2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Konflikt terminów");
    }

    @Test
    @DisplayName("Should update an existing slot successfully")
    void updateSlotWhenValidDataShouldUpdateSlot() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        TimeRange newTimeRange = createTimeRange(12, 13);

        // when
        schedule.updateSlot(slotId, newTimeRange, "Room 102");

        // then
        Slot updatedSlot = schedule.slots().get(0);
        assertThat(updatedSlot.getTimeRange()).isEqualTo(newTimeRange);
        assertThat(updatedSlot.getOffice()).isEqualTo("Room 102");
    }

    @Test
    @DisplayName("Should throw exception when updating slot to overlap with another slot")
    void updateSlotWhenNewTimeOverlapsWithAnotherSlotShouldThrowException() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(
                new AddSlotCommand(createTimeRange(10, 11), "Room 101"),
                new AddSlotCommand(createTimeRange(12, 13), "Room 102")
        ));
        SlotId slotId1 = schedule.slots().get(0).getSlotId();
        TimeRange overlappingTimeRange = createTimeRange(11, 13);

        // when & then
        assertThatThrownBy(() -> schedule.updateSlot(slotId1, overlappingTimeRange, "Room 101"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Konflikt terminów");
    }

    @Test
    @DisplayName("Should allow updating slot time to overlap with its old time")
    void updateSlotWhenNewTimeOverlapsWithItsOldTimeShouldUpdateSuccessfully() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 12), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        TimeRange newTimeRange = createTimeRange(11, 13); // overlaps with 10-12

        // when
        schedule.updateSlot(slotId, newTimeRange, "Room 101");

        // then
        assertThat(schedule.slots().get(0).getTimeRange()).isEqualTo(newTimeRange);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent slot")
    void updateSlotWhenSlotDoesNotExistShouldThrowException() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());

        // when & then
        assertThatThrownBy(() -> schedule.updateSlot(new SlotId(), createTimeRange(10, 11), "Room 101"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Slot not found");
    }

    @Test
    @DisplayName("Should reserve multiple slots successfully")
    void reserveSlotsWhenSlotsExistShouldSetStatusToBooked() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(
                new AddSlotCommand(createTimeRange(10, 11), "Room 101"),
                new AddSlotCommand(createTimeRange(11, 12), "Room 102")
        ));
        List<SlotId> slotIdsToReserve = schedule.slots().stream().map(Slot::getSlotId).toList();

        // when
        schedule.reserveSlots(slotIdsToReserve);

        // then
        assertThat(schedule.slots().get(0).getStatus()).isEqualTo(SlotStatus.BOOKED);
        assertThat(schedule.slots().get(1).getStatus()).isEqualTo(SlotStatus.BOOKED);
    }

    @Test
    @DisplayName("Should throw exception when reserving an already reserved slot")
    void reserveSlotsWhenSlotIsAlreadyBookedShouldThrowException() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        schedule.reserveSlots(List.of(slotId));

        // when & then
        assertThatThrownBy(() -> schedule.reserveSlots(List.of(slotId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Slot is already booked");
    }

    @Test
    @DisplayName("Should free reserved slots successfully")
    void freeSlotsWhenSlotsAreBookedShouldSetStatusToAvailable() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        schedule.reserveSlots(List.of(slotId));

        // when
        schedule.freeSlots(List.of(slotId));

        // then
        assertThat(schedule.slots().get(0).getStatus()).isEqualTo(SlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should remove an available slot successfully")
    void removeSlotWhenSlotIsAvailableShouldRemoveFromSchedule() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();

        // when
        schedule.removeSlot(slotId);

        // then
        assertThat(schedule.slots()).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when removing a booked slot")
    void removeSlotWhenSlotIsBookedShouldThrowException() {
        // given
        Schedule schedule = Schedule.create(mockDoctorId());
        schedule.addTimeSlots(List.of(new AddSlotCommand(createTimeRange(10, 11), "Room 101")));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        schedule.reserveSlots(List.of(slotId));

        // when & then
        assertThatThrownBy(() -> schedule.removeSlot(slotId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only remove available slots");
    }
}
