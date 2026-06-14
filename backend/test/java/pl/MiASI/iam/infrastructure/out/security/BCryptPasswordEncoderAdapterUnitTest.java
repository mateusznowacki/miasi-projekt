package pl.MiASI.iam.infrastructure.out.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderAdapterUnitTest {

    private final BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();

    @Test
    @DisplayName("When encoding a password, it should return a valid bcrypt hash")
    void encodeWhenCalledShouldReturnHashedPassword() {
        // Given
        String rawPassword = "mySecretPassword123";

        // When
        String encoded = adapter.encode(rawPassword);

        // Then
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$") || encoded.startsWith("$2y$"));
    }

    @Test
    @DisplayName("When matching correct password, it should return true")
    void matchesWhenPasswordCorrectShouldReturnTrue() {
        // Given
        String rawPassword = "mySecretPassword123";
        String encoded = adapter.encode(rawPassword);

        // When
        boolean matches = adapter.matches(rawPassword, encoded);

        // Then
        assertTrue(matches);
    }

    @Test
    @DisplayName("When matching incorrect password, it should return false")
    void matchesWhenPasswordIncorrectShouldReturnFalse() {
        // Given
        String rawPassword = "mySecretPassword123";
        String encoded = adapter.encode(rawPassword);

        // When
        boolean matches = adapter.matches("wrongPassword", encoded);

        // Then
        assertFalse(matches);
    }

    @Test
    @DisplayName("When checking empty string against hash, it should return false")
    void matchesWhenEmptyStringProvidedShouldReturnFalse() {
        // Given
        String encoded = adapter.encode("password");

        // When
        boolean matches = adapter.matches("", encoded);

        // Then
        assertFalse(matches);
    }

}
