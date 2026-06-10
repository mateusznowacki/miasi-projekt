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
}