package pl.edu.pwr.MiASI.iam.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "accounts", schema = "iam")
public class AccountJpaEntity {
    @Id
    private UUID id;
    private String email;
    private String passwordHash;
    private String role;
    private String nationalId;

    protected AccountJpaEntity() {}

    public AccountJpaEntity(UUID id, String email, String passwordHash, String role, String nationalId) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.nationalId = nationalId;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getHasloHash() { return passwordHash; }
    public String getRola() { return role; }
    public String getPesel() { return nationalId; }
}
