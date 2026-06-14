package pl.MiASI.iam.infrastructure.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderUnitTest {

    private final JwtTokenProvider provider = new JwtTokenProvider();

    @Test
    @DisplayName("When generating token, it should contain correct account data and be signed")
    void generateTokenWhenCalledShouldReturnValidJwt() {
        // Given
        AccountId accountId = new AccountId();
        Account account = new Account(accountId, "test@test.com", "hash", Role.DOCTOR, true);

        // When
        String token = provider.generateToken(account);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(provider.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(accountId.value().toString(), claims.getSubject());
        assertEquals(Role.DOCTOR.name(), claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    @DisplayName("When token is generated, its expiration date should be exactly one day after issue date")
    void generateTokenWhenCalledShouldSetExpirationToOneDay() {
        // Given
        Account account = new Account(new AccountId(), "test@test.com", "hash", Role.PATIENT, true);

        // When
        String token = provider.generateToken(account);

        // Then
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(provider.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        long diff = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        // 86400000 ms is exactly 1 day
        assertEquals(86400000L, diff);
    }
}
