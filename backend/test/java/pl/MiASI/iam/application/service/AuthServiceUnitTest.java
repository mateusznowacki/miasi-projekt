package pl.MiASI.iam.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.iam.application.port.in.AuthResult;
import pl.MiASI.iam.application.port.out.AccountRepository;
import pl.MiASI.iam.application.port.out.PasswordEncoderPort;
import pl.MiASI.iam.application.port.out.TokenProviderPort;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private AuthService authService;

    private Account testAccount;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String testHash = "hashedPassword";

    @BeforeEach
    void setUp() {
        testAccount = new Account(new AccountId(), testEmail, testHash, Role.PATIENT, true);
    }

    @Test
    @DisplayName("When credentials are valid and account is active, it should return AuthResult")
    void loginWhenValidCredentialsShouldReturnAuthResult() {
        // given
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(testPassword, testHash)).thenReturn(true);
        when(tokenProvider.generateToken(testAccount)).thenReturn("mockedToken");

        // when
        AuthResult result = authService.login(testEmail, testPassword);

        // then
        assertNotNull(result);
        assertEquals(testEmail, result.email());
        assertEquals("mockedToken", result.accessToken());
        assertEquals(Role.PATIENT.name(), result.role());
    }

    @Test
    @DisplayName("When email is not found during login, it should throw exception")
    void loginWhenInvalidEmailShouldThrowException() {
        // given
        when(accountRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.login("wrong@example.com", testPassword));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("When password doesn't match during login, it should throw exception")
    void loginWhenInvalidPasswordShouldThrowException() {
        // given
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPass", testHash)).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.login(testEmail, "wrongPass"));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("When trying to login to inactive account, it should throw exception")
    void loginWhenAccountNotActiveShouldThrowException() {
        // given
        testAccount.deactivate();
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(testPassword, testHash)).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.login(testEmail, testPassword));
        assertEquals("Account not active", exception.getMessage());
    }

    @Test
    @DisplayName("When email not taken, it should successfully register user and return new AccountId")
    void registerUserWhenEmailNotTakenShouldSaveAccountAndReturnId() {
        // given
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(testHash);

        // when
        AccountId newAccountId = authService.registerUser(testEmail, testPassword, Role.PATIENT);

        // then
        assertNotNull(newAccountId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("When registering user with existing email, it should throw exception")
    void registerUserWhenEmailTakenShouldThrowException() {
        // given
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(testAccount));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.registerUser(testEmail, testPassword, Role.PATIENT));
        assertEquals("Email taken", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("When provided token is a valid UUID and account exists, it should activate account")
    void activateAccountWhenValidTokenShouldActivateAccount() {
        // given
        UUID id = testAccount.getAccountId().value();
        testAccount.deactivate();
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));

        // when
        authService.activateAccount(id.toString());

        // then
        assertTrue(testAccount.isActive());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("When activating account with non-UUID token, it should throw exception")
    void activateAccountWhenInvalidTokenFormatShouldThrowException() {
        // given
        String invalidToken = "not-a-uuid";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.activateAccount(invalidToken));
        assertEquals("Invalid activation token", exception.getMessage());
    }

    @Test
    @DisplayName("When activating account but account is not found, it should throw exception")
    void activateAccountWhenAccountNotFoundShouldThrowException() {
        // given
        UUID randomId = UUID.randomUUID();
        when(accountRepository.findById(new AccountId(randomId))).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.activateAccount(randomId.toString()));
        assertEquals("Invalid activation token", exception.getMessage());
    }

    @Test
    @DisplayName("When new email is not taken, it should successfully update email")
    void updateEmailWhenEmailNotTakenShouldUpdateEmail() {
        // given
        String newEmail = "new@example.com";
        when(accountRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));

        // when
        authService.updateEmail(testAccount.getAccountId(), newEmail);

        // then
        assertEquals(newEmail, testAccount.getEmail());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("When updating email to one taken by another account, it should throw exception")
    void updateEmailWhenEmailTakenByOtherAccountShouldThrowException() {
        // given
        String newEmail = "taken@example.com";
        Account otherAccount = new Account(new AccountId(), newEmail, "hash", Role.PATIENT, true);
        
        when(accountRepository.findByEmail(newEmail)).thenReturn(Optional.of(otherAccount));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.updateEmail(testAccount.getAccountId(), newEmail));
        assertEquals("Email zajęty przez inne konto", exception.getMessage());
    }

    @Test
    @DisplayName("When updating email for non-existent account, it should throw exception")
    void updateEmailWhenAccountNotFoundShouldThrowException() {
        // given
        String newEmail = "new@example.com";
        AccountId accountId = testAccount.getAccountId();
        
        when(accountRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.updateEmail(accountId, newEmail));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    @DisplayName("When account found, it should successfully deactivate account")
    void deactivateAccountWhenAccountFoundShouldDeactivate() {
        // given
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));

        // when
        authService.deactivateAccount(testAccount.getAccountId());

        // then
        assertFalse(testAccount.isActive());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("When deactivating non-existent account, it should throw exception")
    void deactivateAccountWhenAccountNotFoundShouldThrowException() {
        // given
        AccountId accountId = testAccount.getAccountId();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.deactivateAccount(accountId));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    @DisplayName("When account found, it should successfully update role")
    void updateRoleWhenAccountFoundShouldUpdateRole() {
        // given
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));

        // when
        authService.updateRole(testAccount.getAccountId(), Role.DOCTOR);

        // then
        assertEquals(Role.DOCTOR, testAccount.getRole());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("When updating role for non-existent account, it should throw exception")
    void updateRoleWhenAccountNotFoundShouldThrowException() {
        // given
        AccountId accountId = testAccount.getAccountId();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.updateRole(accountId, Role.DOCTOR));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    @DisplayName("When logging out, it should not throw any exception")
    void logoutWhenCalledShouldNotThrowException() {
        // when & then
        assertDoesNotThrow(() -> authService.logout("someToken"));
    }
}
