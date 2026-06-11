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
    public ResponseEntity<AuthResult> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authUseCase.login(req.email(), req.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authUseCase.logout(token.substring(7));
        } else if (token != null) {
            authUseCase.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activate(@RequestParam String token) {
        try {
            authUseCase.activateAccount(token);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

record LoginRequest(String email, String password) {
}