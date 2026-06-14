package pl.MiASI.iam.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountUnitTest {

    @Test
    @DisplayName("When creating a new account, it should set correct values and be inactive")
    void createWhenCalledShouldSetCorrectValuesAndBeInactive() {
        // Given
        String email = "test@example.com";
        String passwordHash = "hash123";
        Role role = Role.PATIENT;

        // When
        Account account = Account.create(email, passwordHash, role);

        // Then
        assertNotNull(account.getAccountId());
        assertNotNull(account.getAccountId().value());
        assertEquals(email, account.getEmail());
        assertEquals(passwordHash, account.getPasswordHash());
        assertEquals(role, account.getRole());
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("When activating an account, it should become active")
    void activateWhenCalledShouldSetAccountToActive() {
        // Given
        Account account = Account.create("test@example.com", "hash", Role.PATIENT);

        // When
        account.activate();

        // Then
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("When deactivating an account, it should become inactive")
    void deactivateWhenCalledShouldSetAccountToInactive() {
        // Given
        Account account = Account.create("test@example.com", "hash", Role.PATIENT);
        account.activate();

        // When
        account.deactivate();

        // Then
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("When deactivating an already inactive account, it should remain inactive")
    void deactivateWhenAlreadyInactiveShouldRemainInactive() {
        // Given
        Account account = Account.create("test@example.com", "hash", Role.PATIENT);

        // When
        account.deactivate();

        // Then
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("When updating role, it should change the role")
    void updateRoleWhenCalledShouldChangeRole() {
        // Given
        Account account = Account.create("test@example.com", "hash", Role.PATIENT);
        Role newRole = Role.ADMIN;

        // When
        account.updateRole(newRole);

        // Then
        assertEquals(newRole, account.getRole());
    }

    @Test
    @DisplayName("When updating email, it should change the email")
    void updateEmailWhenCalledShouldChangeEmail() {
        // Given
        Account account = Account.create("test@example.com", "hash", Role.PATIENT);
        String newEmail = "new@example.com";

        // When
        account.updateEmail(newEmail);

        // Then
        assertEquals(newEmail, account.getEmail());
    }

    @Test
    @DisplayName("When using constructor, it should assign all fields correctly")
    void constructorWhenCalledShouldAssignAllFields() {
        // Given
        AccountId id = new AccountId();
        String email = "mail@mail.com";
        String hash = "123";
        Role role = Role.DOCTOR;
        boolean active = true;

        // When
        Account account = new Account(id, email, hash, role, active);

        // Then
        assertEquals(id, account.getAccountId());
        assertEquals(email, account.getEmail());
        assertEquals(hash, account.getPasswordHash());
        assertEquals(role, account.getRole());
        assertTrue(account.isActive());
    }
}
