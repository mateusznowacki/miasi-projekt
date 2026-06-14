package pl.MiASI.iam.infrastructure.out.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SecurityConfigUnitTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("When instantiating SecurityConfig, it should be created successfully")
    void constructorWhenCalledShouldCreateInstance() {
        // When
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);

        // Then
        assertNotNull(config);
    }
}
