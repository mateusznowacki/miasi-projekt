package pl.MiASI.iam.infrastructure.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.MiASI.iam.application.port.in.AuthResult;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.infrastructure.out.security.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthUseCase authUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldLoginAndReturnAuthResult() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResult result = new AuthResult("user-123", "test@example.com", "PATIENT", "mock-jwt-token");

        when(authUseCase.login(anyString(), anyString())).thenReturn(result);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token"));

        verify(authUseCase, times(1)).login("test@example.com", "password123");
    }

    @Test
    void shouldLogoutWithBearerToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());

        verify(authUseCase, times(1)).logout("valid-token");
    }

    @Test
    void shouldLogoutWithRawToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "valid-token"))
                .andExpect(status().isOk());

        verify(authUseCase, times(1)).logout("valid-token");
    }

    @Test
    void shouldActivateAccountSuccessfully() throws Exception {
        mockMvc.perform(get("/api/auth/activate")
                .param("token", "activation-token"))
                .andExpect(status().isOk());

        verify(authUseCase, times(1)).activateAccount("activation-token");
    }

    @Test
    void shouldReturnBadRequestWhenActivationFails() throws Exception {
        doThrow(new IllegalArgumentException("Invalid token")).when(authUseCase).activateAccount("invalid-token");

        mockMvc.perform(get("/api/auth/activate")
                .param("token", "invalid-token"))
                .andExpect(status().isBadRequest());

        verify(authUseCase, times(1)).activateAccount("invalid-token");
    }
}
