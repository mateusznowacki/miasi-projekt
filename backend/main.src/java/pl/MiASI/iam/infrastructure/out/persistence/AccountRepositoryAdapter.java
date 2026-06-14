package pl.MiASI.iam.infrastructure.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.port.out.AccountRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class AccountRepositoryAdapter implements AccountRepository {
    private final SpringDataAccountRepository repo;

    @Override
    public void save(Account account) {
        AccountJpaEntity e = repo.findById(account.getAccountId().value()).orElse(new AccountJpaEntity());
        e.setId(account.getAccountId().value());
        e.setEmail(account.getEmail());
        e.setPasswordHash(account.getPasswordHash());
        e.setRole(account.getRole());
        e.setActive(account.isActive());
        repo.save(e);
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return repo.findById(id.value()).map(e -> new Account(new AccountId(e.getId()), e.getEmail(), e.getPasswordHash(), e.getRole(), e.isActive()));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return repo.findByEmail(email).map(e -> new Account(new AccountId(e.getId()), e.getEmail(), e.getPasswordHash(), e.getRole(), e.isActive()));
    }
}