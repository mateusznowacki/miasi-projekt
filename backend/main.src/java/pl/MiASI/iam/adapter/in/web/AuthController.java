package pl.MiASI.iam.adapter.in.web;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.iam.application.port.in.AuthResult;
import pl.MiASI.iam.application.port.in.AuthUseCase;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCase authUseCase;
    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@RequestBody LoginRequest req) { return ResponseEntity.ok(authUseCase.login(req.email(), req.password())); }
}
record LoginRequest(String email, String password) {}