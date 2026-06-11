package pl.MiASI.iam.domain.model;

import lombok.Getter;

@Getter
public class Account {
    private final AccountId accountId;
    private final String passwordHash;
    private String email;
    private Role role;
    private boolean active;

    public Account(AccountId accountId, String email, String passwordHash, Role role, boolean active) {
        this.accountId = accountId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public static Account create(String email, String passwordHash, Role role) {
        return new Account(new AccountId(), email, passwordHash, role, false); // By default not active
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}