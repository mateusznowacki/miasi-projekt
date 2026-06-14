package pl.MiASI.medicalcare.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlotUnitTest {

    private TimeRange createTimeRange(int startHour, int endHour) {
        return new TimeRange(
                LocalDateTime.of(2023, 10, 10, startHour, 0),
                LocalDateTime.of(2023, 10, 10, endHour, 0)
        );
    }

    @Test
    @DisplayName("Should create available slot")
    void createWhenValidDataShouldReturnAvailableSlot() {
        // given
        TimeRange timeRange = createTimeRange(10, 11);
        String office = "Room 101";

        // when
        Slot slot = Slot.create(timeRange, office);

        // then
        assertThat(slot.getSlotId()).isNotNull();
        assertThat(slot.getTimeRange()).isEqualTo(timeRange);
        assertThat(slot.getOffice()).isEqualTo(office);
        assertThat(slot.getStatus()).isEqualTo(SlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should reserve an available slot")
    void reserveWhenSlotIsAvailableShouldSetStatusToBooked() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");

        // when
        slot.reserve();

        // then
        assertThat(slot.getStatus()).isEqualTo(SlotStatus.BOOKED);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when reserving an already booked slot")
    void reserveWhenSlotIsBookedShouldThrowException() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        slot.reserve();

        // when & then
        assertThatThrownBy(slot::reserve)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Slot is already booked");
    }

    @Test
    @DisplayName("Should free a booked slot")
    void freeWhenSlotIsBookedShouldSetStatusToAvailable() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        slot.reserve();

        // when
        slot.free();

        // then
        assertThat(slot.getStatus()).isEqualTo(SlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should update an available slot with new time range and office")
    void updateWhenSlotIsAvailableShouldUpdateFields() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        TimeRange newTimeRange = createTimeRange(12, 13);
        String newOffice = "Room 102";

        // when
        slot.update(newTimeRange, newOffice);

        // then
        assertThat(slot.getTimeRange()).isEqualTo(newTimeRange);
        assertThat(slot.getOffice()).isEqualTo(newOffice);
    }

    @Test
    @DisplayName("Should partially update an available slot (only time range)")
    void updateWhenOnlyTimeRangeProvidedShouldUpdateTimeRange() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        TimeRange newTimeRange = createTimeRange(12, 13);

        // when
        slot.update(newTimeRange, null);

        // then
        assertThat(slot.getTimeRange()).isEqualTo(newTimeRange);
        assertThat(slot.getOffice()).isEqualTo("Room 101");
    }

    @Test
    @DisplayName("Should partially update an available slot (only office)")
    void updateWhenOnlyOfficeProvidedShouldUpdateOffice() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        String newOffice = "Room 102";

        // when
        slot.update(null, newOffice);

        // then
        assertThat(slot.getTimeRange()).isNotNull();
        assertThat(slot.getOffice()).isEqualTo(newOffice);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a booked slot")
    void updateWhenSlotIsBookedShouldThrowException() {
        // given
        Slot slot = Slot.create(createTimeRange(10, 11), "Room 101");
        slot.reserve();

        // when & then
        assertThatThrownBy(() -> slot.update(createTimeRange(12, 13), "Room 102"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only update available slots");
    }
}
