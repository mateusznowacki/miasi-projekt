package pl.MiASI.medicalcare.domain.model;

import pl.MiASI.shared.domain.model.DoctorId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Schedule {
    private final ScheduleId scheduleId;
    private final DoctorId doctorId;
    private final List<Slot> slots;

    public Schedule(ScheduleId scheduleId, DoctorId doctorId, List<Slot> slots) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.slots = new ArrayList<>(slots);
    }

    public static Schedule create(DoctorId doctorId) {
        return new Schedule(new ScheduleId(), doctorId, new ArrayList<>());
    }

    public void addTimeSlots(List<pl.MiASI.medicalcare.application.port.in.AddSlotCommand> commands) {
        for (pl.MiASI.medicalcare.application.port.in.AddSlotCommand cmd : commands) {
            this.slots.add(Slot.create(cmd.timeRange(), cmd.office()));
        }
    }

    public void updateSlot(SlotId slotId, TimeRange newTimeRange, String newOffice) {
        Slot slot = findSlot(slotId);
        slot.update(newTimeRange, newOffice);
    }

    public void reserveSlots(List<SlotId> slotIds) {
        for (SlotId id : slotIds) {
            Slot slot = findSlot(id);
            slot.reserve();
        }
    }

    public void freeSlots(List<SlotId> slotIds) {
        for (SlotId id : slotIds) {
            Slot slot = findSlot(id);
            slot.free();
        }
    }

    private Slot findSlot(SlotId id) {
        return slots.stream()
            .filter(s -> s.getSlotId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + id.value()));
    }
}