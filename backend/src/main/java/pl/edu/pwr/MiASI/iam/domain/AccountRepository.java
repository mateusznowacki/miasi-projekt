package pl.edu.pwr.MiASI.iam.domain;

import java.util.Optional;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(AccountId id);
    Optional<Account> findByEmail(Email email);
}
