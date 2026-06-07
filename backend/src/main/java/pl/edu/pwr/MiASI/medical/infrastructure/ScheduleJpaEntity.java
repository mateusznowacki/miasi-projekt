package pl.edu.pwr.MiASI.medical.infrastructure;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules", schema = "medical")
public class ScheduleJpaEntity {
    @Id
    private UUID id;
    private UUID lekarzId;

    @ElementCollection
    @CollectionTable(name = "schedule_slots", schema = "medical", joinColumns = @JoinColumn(name = "schedule_id"))
    private List<SlotJpaEmbeddable> slots;

    protected ScheduleJpaEntity() {}

    public ScheduleJpaEntity(UUID id, UUID lekarzId, List<SlotJpaEmbeddable> slots) {
        this.id = id;
        this.lekarzId = lekarzId;
        this.slots = slots;
    }

    public UUID getId() { return id; }
    public UUID getLekarzId() { return lekarzId; }
    public List<SlotJpaEmbeddable> getSloty() { return slots; }
}

@Embeddable
class SlotJpaEmbeddable {
    private UUID slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String stan;

    protected SlotJpaEmbeddable() {}

    public SlotJpaEmbeddable(UUID slotId, LocalDateTime startTime, LocalDateTime endTime, String stan) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stan = stan;
    }

    public UUID getSlotId() { return slotId; }
    public LocalDateTime getOdKiedy() { return startTime; }
    public LocalDateTime getDoKiedy() { return endTime; }
    public String getStan() { return stan; }
}
