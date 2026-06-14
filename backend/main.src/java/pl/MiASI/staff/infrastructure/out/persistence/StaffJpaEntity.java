package pl.MiASI.staff.infrastructure.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.UUID;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
class StaffJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StaffRole role;

    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private String specialization;
    private String pwz;
    private String department;
    private String position;
    private String workSchedule;
}