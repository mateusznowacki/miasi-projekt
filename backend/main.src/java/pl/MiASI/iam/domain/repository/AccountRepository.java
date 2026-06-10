package pl.MiASI.iam.domain.repository;
import pl.MiASI.iam.domain.model.Account;
import pl.MiASI.iam.domain.model.AccountId;
import java.util.Optional;
public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(AccountId accountId);
    Optional<Account> findByEmail(String email);
}