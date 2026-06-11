package pl.MiASI.iam.adapter.out.persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.MiASI.iam.domain.model.Role;
import java.util.UUID;
@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor
public class AccountJpaEntity {
    @Id private UUID id;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING) private Role role;
    @Column(nullable = false) private boolean active = false;
}