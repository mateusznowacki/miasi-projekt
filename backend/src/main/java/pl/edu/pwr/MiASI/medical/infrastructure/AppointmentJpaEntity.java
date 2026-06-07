package pl.edu.pwr.MiASI.medical.infrastructure;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments", schema = "medical")
public class AppointmentJpaEntity {
    @Id
    private UUID id;
    private UUID patientId;
    private UUID lekarzId;
    private String typ;
    private String status;
    
    @ElementCollection
    @CollectionTable(name = "appointment_slots", schema = "medical", joinColumns = @JoinColumn(name = "appointment_id"))
    @Column(name = "slot_id")
    private List<UUID> slots;

    protected AppointmentJpaEntity() {}

    public AppointmentJpaEntity(UUID id, UUID patientId, UUID lekarzId, String typ, String status, List<UUID> slots) {
        this.id = id;
        this.patientId = patientId;
        this.lekarzId = lekarzId;
        this.typ = typ;
        this.status = status;
        this.slots = slots;
    }

    public UUID getId() { return id; }
    public UUID getPacjentId() { return patientId; }
    public UUID getLekarzId() { return lekarzId; }
    public String getTyp() { return typ; }
    public String getStatus() { return status; }
    public List<UUID> getSloty() { return slots; }
}
