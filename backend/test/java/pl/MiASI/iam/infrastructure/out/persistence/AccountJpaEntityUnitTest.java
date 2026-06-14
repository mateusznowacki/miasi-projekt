package pl.MiASI.iam.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.iam.application.domain.model.Role;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountJpaEntityUnitTest {

    @Test
    @DisplayName("When using setters and getters, values should be correctly stored and retrieved")
    void settersAndGettersWhenCalledShouldStoreAndRetrieveValues() {
        // Given
        AccountJpaEntity entity = new AccountJpaEntity();
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String hash = "hash123";
        Role role = Role.PATIENT;

        // When
        entity.setId(id);
        entity.setEmail(email);
        entity.setPasswordHash(hash);
        entity.setRole(role);
        entity.setActive(true);

        // Then
        assertEquals(id, entity.getId());
        assertEquals(email, entity.getEmail());
        assertEquals(hash, entity.getPasswordHash());
        assertEquals(role, entity.getRole());
        assertTrue(entity.isActive());
    }

    @Test
    @DisplayName("When created using default constructor, active should be false by default")
    void defaultConstructorWhenCalledShouldSetDefaultValues() {
        // When
        AccountJpaEntity entity = new AccountJpaEntity();

        // Then
        assertFalse(entity.isActive());
    }
}
