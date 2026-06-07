package pl.edu.pwr.MiASI.iam.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.iam.domain.*;
import java.util.Optional;

@Component
public class AccountRepositoryAdapter implements AccountRepository {
    private final SpringDataAccountRepository springDataKontoRepository;

    public AccountRepositoryAdapter(SpringDataAccountRepository springDataKontoRepository) {
        this.springDataKontoRepository = springDataKontoRepository;
    }

    @Override
    public void save(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity(
            account.getId().id(),
            account.getEmail().value(),
            account.getHasloHash(),
            account.getRola().name(),
            account.getPesel().value()
        );
        springDataKontoRepository.save(entity);
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return springDataKontoRepository.findById(id.id()).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByEmail(Email email) {
        return springDataKontoRepository.findByEmail(email.value()).map(this::toDomain);
    }

    private Account toDomain(AccountJpaEntity entity) {
        return new Account(
            new AccountId(entity.getId()),
            new Email(entity.getEmail()),
            entity.getHasloHash(),
            Role.valueOf(entity.getRola()),
            new NationalId(entity.getPesel())
        );
    }
}
