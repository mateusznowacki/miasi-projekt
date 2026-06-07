package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.List;
import java.util.ArrayList;

@AggregateRoot
public class Schedule {
    private ScheduleId id;
    private DoctorId lekarzId;
    private List<Slot> slots = new ArrayList<>();

    public Schedule(ScheduleId id, DoctorId lekarzId) {
        this.id = id;
        this.lekarzId = lekarzId;
    }

    public void addSlots(List<Slot> newSlots) {
        this.slots.addAll(newSlots);
    }

    public void bookSlots(List<SlotId> selectedSlotIds) {
        for (SlotId slotId : selectedSlotIds) {
            Slot slot = slots.stream()
                .filter(s -> s.getId().equals(slotId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Slot nie istnieje w harmonogramie"));
            slot.book();
        }
    }

    public void releaseSlots(List<SlotId> releasedSlotIds) {
        for (SlotId slotId : releasedSlotIds) {
            slots.stream()
                .filter(s -> s.getId().equals(slotId))
                .findFirst()
                .ifPresent(Slot::release);
        }
    }

    public ScheduleId getId() { return id; }
    public DoctorId getLekarzId() { return lekarzId; }
    public List<Slot> getSloty() { return slots; }
}
