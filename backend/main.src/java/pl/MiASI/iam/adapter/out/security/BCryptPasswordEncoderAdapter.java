package pl.MiASI.iam.adapter.out.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.MiASI.iam.application.port.out.PasswordEncoderPort;
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override public String encode(String raw) { return encoder.encode(raw); }
    @Override public boolean matches(String raw, String enc) { return encoder.matches(raw, enc); }
}