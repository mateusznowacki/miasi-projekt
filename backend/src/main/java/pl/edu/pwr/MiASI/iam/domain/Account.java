package pl.edu.pwr.MiASI.iam.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class Account {
    private AccountId id;
    private Email email;
    private String passwordHash;
    private Role role;
    private NationalId nationalId;
    private List<Consent> consents = new ArrayList<>();

    public Account(AccountId id, Email email, String passwordHash, Role role, NationalId nationalId) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.nationalId = nationalId;
    }

    public static Account register(Email email, String passwordHash, NationalId nationalId) {
        return new Account(new AccountId(UUID.randomUUID()), email, passwordHash, Role.PACJENT, nationalId);
    }

    public void updatePassword(String noweHasloHash) {
        this.passwordHash = noweHasloHash;
    }

    public void manageConsents(List<Consent> noweZgody) {
        this.consents = noweZgody;
    }

    // Getters
    public AccountId getId() { return id; }
    public Email getEmail() { return email; }
    public String getHasloHash() { return passwordHash; }
    public Role getRola() { return role; }
    public NationalId getPesel() { return nationalId; }
    public List<Consent> getZgody() { return consents; }
}
