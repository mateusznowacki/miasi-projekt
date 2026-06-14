package pl.MiASI.iam.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountIdUnitTest {

    @Test
    @DisplayName("When using default constructor, it should generate a random UUID")
    void defaultConstructorWhenCalledShouldGenerateUUID() {
        // When
        AccountId accountId = new AccountId();

        // Then
        assertNotNull(accountId.value());
    }

    @Test
    @DisplayName("When using parameterized constructor, it should set the provided UUID")
    void parameterizedConstructorWhenCalledShouldSetProvidedUUID() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        AccountId accountId = new AccountId(uuid);

        // Then
        assertEquals(uuid, accountId.value());
    }
}
