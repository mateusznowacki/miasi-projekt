package pl.MiASI.iam.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.MiASI.iam.application.port.in.AuthResult;
import pl.MiASI.iam.application.port.in.AuthUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private AuthUseCase authUseCase;

    @InjectMocks
    private AuthController controller;

    @Test
    @DisplayName("When logging in with valid credentials, it should return OK and the token")
    void loginWhenCredentialsValidShouldReturnOkAndToken() {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "pass");
        AuthResult expectedResult = new AuthResult("1", "test@test.com", "ROLE", "token");
        when(authUseCase.login(request.email(), request.password())).thenReturn(expectedResult);

        // When
        ResponseEntity<AuthResult> response = controller.login(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(authUseCase).login(request.email(), request.password());
    }

    @Test
    @DisplayName("When logging out with Bearer token, it should trim prefix and call use case")
    void logoutWhenTokenStartsWithBearerShouldTrimAndLogout() {
        // Given
        String header = "Bearer my.jwt.token";

        // When
        ResponseEntity<Void> response = controller.logout(header);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUseCase).logout("my.jwt.token");
    }

    @Test
    @DisplayName("When logging out without Bearer prefix, it should call use case with original token")
    void logoutWhenTokenWithoutBearerShouldLogoutWithOriginalToken() {
        // Given
        String header = "my.jwt.token";

        // When
        ResponseEntity<Void> response = controller.logout(header);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUseCase).logout("my.jwt.token");
    }

    @Test
    @DisplayName("When logging out without providing a token, it should return OK but not call use case")
    void logoutWhenTokenNullShouldReturnOkButNotCallLogout() {
        // Given
        String header = null;

        // When
        ResponseEntity<Void> response = controller.logout(header);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUseCase, never()).logout(anyString());
    }

    @Test
    @DisplayName("When logging out with just 'Bearer ', it should extract empty string")
    void logoutWhenTokenOnlyBearerShouldExtractEmptyString() {
        // Given
        String header = "Bearer ";

        // When
        ResponseEntity<Void> response = controller.logout(header);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUseCase).logout("");
    }

    @Test
    @DisplayName("When activating with valid token, it should return OK")
    void activateWhenTokenValidShouldReturnOk() {
        // Given
        String token = "valid-token";
        doNothing().when(authUseCase).activateAccount(token);

        // When
        ResponseEntity<Void> response = controller.activate(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authUseCase).activateAccount(token);
    }

    @Test
    @DisplayName("When activating with invalid token, it should catch exception and return Bad Request")
    void activateWhenTokenInvalidShouldReturnBadRequest() {
        // Given
        String token = "invalid-token";
        doThrow(new IllegalArgumentException("Invalid")).when(authUseCase).activateAccount(token);

        // When
        ResponseEntity<Void> response = controller.activate(token);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authUseCase).activateAccount(token);
    }
}
