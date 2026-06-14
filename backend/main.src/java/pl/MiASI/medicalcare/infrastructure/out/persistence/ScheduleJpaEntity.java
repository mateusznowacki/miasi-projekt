package pl.MiASI.medicalcare.infrastructure.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
class ScheduleJpaEntity {

    @Id
    private UUID id;

    @Column(unique = true)
    private UUID doctorId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<SlotJpaEntity> slots;
}
