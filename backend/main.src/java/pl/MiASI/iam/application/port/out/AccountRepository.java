package pl.MiASI.iam.application.port.out;

import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;

import java.util.Optional;

public interface AccountRepository {
    void save(Account account);

    Optional<Account> findById(AccountId accountId);

    Optional<Account> findByEmail(String email);
}