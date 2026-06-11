package pl.MiASI.iam.application.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.iam.application.port.in.AuthResult;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.application.port.out.PasswordEncoderPort;
import pl.MiASI.iam.application.port.out.TokenProviderPort;
import pl.MiASI.iam.domain.model.Account;
import pl.MiASI.iam.domain.model.AccountId;
import pl.MiASI.iam.domain.model.Role;
import pl.MiASI.iam.domain.repository.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AccountRepository accountRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    @Override
    @Transactional
    public AuthResult login(String email, String password) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, account.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
        if (!account.isActive()) throw new IllegalArgumentException("Account not active");
        return new AuthResult(account.getAccountId().value().toString(), account.getEmail(), account.getRole().name(), tokenProvider.generateToken(account));
    }

    @Override
    @Transactional
    public AccountId registerUser(String email, String password, Role role) {
        if (accountRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email taken");
        Account account = Account.create(email, passwordEncoder.encode(password), role);
        accountRepository.save(account);
        return account.getAccountId();
    }

    @Override
    @Transactional
    public void activateAccount(String token) {
        try {
            java.util.UUID id = java.util.UUID.fromString(token);
            Account account = accountRepository.findById(new AccountId(id)).orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));
            account.activate();
            accountRepository.save(account);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid activation token");
        }
    }

    @Override
    public void logout(String token) {
        // Here we could implement token blacklisting logic if needed
    }

    @Override
    @Transactional
    public void updateEmail(AccountId accountId, String newEmail) {
        Optional<Account> existing = accountRepository.findByEmail(newEmail);
        if (existing.isPresent() && !existing.get().getAccountId().equals(accountId)) {
            throw new IllegalArgumentException("Email zajęty przez inne konto");
        }
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.updateEmail(newEmail);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deactivateAccount(AccountId accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.deactivate();
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateRole(AccountId accountId, Role role) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.updateRole(role);
        accountRepository.save(account);
    }
}