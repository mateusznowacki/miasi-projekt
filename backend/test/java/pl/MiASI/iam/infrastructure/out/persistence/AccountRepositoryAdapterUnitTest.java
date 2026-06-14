package pl.MiASI.iam.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryAdapterUnitTest {

    @Mock
    private SpringDataAccountRepository repo;

    @InjectMocks
    private AccountRepositoryAdapter adapter;

    @Test
    @DisplayName("When saving a new account, it should save a correct JPA entity")
    void saveWhenNewAccountShouldSaveJpaEntity() {
        // Given
        AccountId accountId = new AccountId();
        Account account = new Account(accountId, "test@test.com", "hash", Role.PATIENT, true);
        
        when(repo.findById(accountId.value())).thenReturn(Optional.empty());

        // When
        adapter.save(account);

        // Then
        verify(repo).findById(accountId.value());
        verify(repo).save(argThat(entity -> 
            entity.getId().equals(accountId.value()) &&
            entity.getEmail().equals("test@test.com") &&
            entity.getPasswordHash().equals("hash") &&
            entity.getRole() == Role.PATIENT &&
            entity.isActive()
        ));
    }

    @Test
    @DisplayName("When saving an existing account, it should update the existing JPA entity")
    void saveWhenExistingAccountShouldUpdateJpaEntity() {
        // Given
        AccountId accountId = new AccountId();
        Account account = new Account(accountId, "new@test.com", "newhash", Role.ADMIN, false);
        
        AccountJpaEntity existingEntity = new AccountJpaEntity();
        existingEntity.setId(accountId.value());
        existingEntity.setEmail("old@test.com");
        existingEntity.setActive(true);
        
        when(repo.findById(accountId.value())).thenReturn(Optional.of(existingEntity));

        // When
        adapter.save(account);

        // Then
        verify(repo).save(argThat(entity -> 
            entity.getId().equals(accountId.value()) &&
            entity.getEmail().equals("new@test.com") &&
            entity.getPasswordHash().equals("newhash") &&
            entity.getRole() == Role.ADMIN &&
            !entity.isActive()
        ));
    }

    @Test
    @DisplayName("When finding by ID and it exists, it should return the mapped Account domain object")
    void findByIdWhenExistsShouldReturnAccount() {
        // Given
        UUID id = UUID.randomUUID();
        AccountId accountId = new AccountId(id);
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(id);
        entity.setEmail("test@test.com");
        entity.setPasswordHash("hash");
        entity.setRole(Role.DOCTOR);
        entity.setActive(true);
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Account> result = adapter.findById(accountId);

        // Then
        assertTrue(result.isPresent());
        Account account = result.get();
        assertEquals(id, account.getAccountId().value());
        assertEquals("test@test.com", account.getEmail());
        assertEquals("hash", account.getPasswordHash());
        assertEquals(Role.DOCTOR, account.getRole());
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("When finding by ID and it does not exist, it should return an empty Optional")
    void findByIdWhenNotExistsShouldReturnEmpty() {
        // Given
        UUID id = UUID.randomUUID();
        AccountId accountId = new AccountId(id);
        when(repo.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Account> result = adapter.findById(accountId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("When finding by email and it exists, it should return the mapped Account domain object")
    void findByEmailWhenExistsShouldReturnAccount() {
        // Given
        String email = "test@test.com";
        UUID id = UUID.randomUUID();
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(id);
        entity.setEmail(email);
        entity.setPasswordHash("hash");
        entity.setRole(Role.PATIENT);
        entity.setActive(false);
        
        when(repo.findByEmail(email)).thenReturn(Optional.of(entity));

        // When
        Optional<Account> result = adapter.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        Account account = result.get();
        assertEquals(id, account.getAccountId().value());
        assertEquals(email, account.getEmail());
        assertEquals(Role.PATIENT, account.getRole());
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("When finding by email and it does not exist, it should return an empty Optional")
    void findByEmailWhenNotExistsShouldReturnEmpty() {
        // Given
        String email = "notfound@test.com";
        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Account> result = adapter.findByEmail(email);

        // Then
        assertTrue(result.isEmpty());
    }
}
