package pl.MiASI.medicalcare.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.MiASI.medicalcare.domain.model.ConsultationType;
import pl.MiASI.medicalcare.domain.model.VisitStatus;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
public class VisitJpaEntity {

    @Id
    private UUID id;

    private UUID patientId;
    private UUID doctorId;

    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    @ElementCollection
    @CollectionTable(name = "visit_slots", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "slot_id")
    private List<UUID> slotIds;
}
