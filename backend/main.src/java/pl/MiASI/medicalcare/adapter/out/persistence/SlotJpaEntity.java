package pl.MiASI.medicalcare.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.MiASI.medicalcare.domain.model.SlotStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "slots")
@Getter
@Setter
@NoArgsConstructor
public class SlotJpaEntity {

    @Id
    private UUID id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String office;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;
}
