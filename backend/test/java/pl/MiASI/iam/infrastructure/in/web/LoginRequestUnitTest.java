package pl.MiASI.iam.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestUnitTest {

    @Test
    @DisplayName("When creating LoginRequest, it should store fields correctly")
    void constructorWhenCalledShouldStoreFields() {
        // Given
        String email = "mail@example.com";
        String password = "secretPassword";

        // When
        LoginRequest req = new LoginRequest(email, password);

        // Then
        assertEquals(email, req.email());
        assertEquals(password, req.password());
    }
}
