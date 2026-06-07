package pl.edu.pwr.MiASI.iam.infrastructure.primary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.MiASI.iam.domain.Email;
import pl.edu.pwr.MiASI.iam.domain.Account;
import pl.edu.pwr.MiASI.iam.domain.NationalId;
import pl.edu.pwr.MiASI.iam.domain.AccountRepository;

@RestController
@RequestMapping("/api/iam")
public class AccountController {
    private final AccountRepository kontoRepository;

    public AccountController(AccountRepository kontoRepository) {
        this.kontoRepository = kontoRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RejestracjaRequest request) {
        Account noweKonto = Account.register(
            new Email(request.email()),
            request.passwordHash(), // TODO: use password encoder
            new NationalId(request.nationalId())
        );
        kontoRepository.save(noweKonto);
        return ResponseEntity.ok().build();
    }
}

record RejestracjaRequest(String email, String passwordHash, String nationalId) {}
