package pl.MiASI.iam.domain.model;
import lombok.Getter;
@Getter
public class Account {
    private final AccountId accountId;
    private final String email;
    private String passwordHash;
    private final Role role;
    public Account(AccountId accountId, String email, String passwordHash, Role role) {
        this.accountId = accountId; this.email = email; this.passwordHash = passwordHash; this.role = role;
    }
    public static Account create(String email, String passwordHash, Role role) {
        return new Account(new AccountId(), email, passwordHash, role);
    }
}