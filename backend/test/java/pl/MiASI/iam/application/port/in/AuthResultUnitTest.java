package pl.MiASI.iam.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthResultUnitTest {

    @Test
    @DisplayName("When creating AuthResult, it should store fields correctly")
    void constructorWhenCalledShouldStoreFields() {
        // Given
        String userId = "user-123";
        String email = "test@example.com";
        String role = "ADMIN";
        String token = "jwt-token-xyz";

        // When
        AuthResult result = new AuthResult(userId, email, role, token);

        // Then
        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(role, result.role());
        assertEquals(token, result.accessToken());
    }
}
