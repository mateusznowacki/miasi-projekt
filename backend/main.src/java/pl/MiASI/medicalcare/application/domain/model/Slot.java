package pl.MiASI.medicalcare.application.domain.model;

import lombok.Getter;

@Getter
public class Slot {
    private final SlotId slotId;
    private TimeRange timeRange;
    private String office;
    private SlotStatus status;

    public Slot(SlotId slotId, TimeRange timeRange, String office, SlotStatus status) {
        this.slotId = slotId;
        this.timeRange = timeRange;
        this.office = office;
        this.status = status;
    }

    public static Slot create(TimeRange timeRange, String office) {
        return new Slot(new SlotId(), timeRange, office, SlotStatus.AVAILABLE);
    }

    public void reserve() {
        if (this.status == SlotStatus.BOOKED) {
            throw new IllegalStateException("Slot is already booked");
        }
        this.status = SlotStatus.BOOKED;
    }

    public void free() {
        this.status = SlotStatus.AVAILABLE;
    }

    public void update(TimeRange timeRange, String office) {
        if (this.status != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Can only update available slots");
        }
        if (timeRange != null) this.timeRange = timeRange;
        if (office != null) this.office = office;
    }
}