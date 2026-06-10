package pl.MiASI.iam.adapter.out.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;
import pl.MiASI.iam.application.port.out.TokenProviderPort;
import pl.MiASI.iam.domain.model.Account;

import java.security.Key;
import java.util.Date;

@Component
@Getter
public class JwtTokenProvider implements TokenProviderPort {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String generateToken(Account account) {
        return Jwts.builder()
                .setSubject(account.getAccountId().value().toString())
                .claim("role", account.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key)
                .compact();
    }
}