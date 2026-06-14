package pl.MiASI.iam.infrastructure.out.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterUnitTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private Key testKey;

    @BeforeEach
    void setUp() {
        testKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("When there is no Authorization header, it should continue the filter chain without authentication")
    void doFilterInternalWhenNoHeaderShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the Authorization header does not start with Bearer, it should continue without authentication")
    void doFilterInternalWhenHeaderInvalidShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic user:pass");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the token is valid, it should set authentication in the SecurityContext and continue")
    void doFilterInternalWhenTokenValidShouldSetAuthentication() throws ServletException, IOException {
        // Given
        UUID userId = UUID.randomUUID();
        String validToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", "DOCTOR")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(testKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProvider.getKey()).thenReturn(testKey);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userId.toString(), auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the token is invalid due to wrong signature, it should clear context and continue")
    void doFilterInternalWhenTokenInvalidSignatureShouldClearContext() throws ServletException, IOException {
        // Given
        Key differentKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String invalidToken = Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .claim("role", "DOCTOR")
                .setIssuedAt(new Date())
                .signWith(differentKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(tokenProvider.getKey()).thenReturn(testKey);
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the token is malformed, it should clear context and continue")
    void doFilterInternalWhenTokenMalformedShouldClearContext() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.format");
        when(tokenProvider.getKey()).thenReturn(testKey);
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the token is expired, it should clear context and continue")
    void doFilterInternalWhenTokenExpiredShouldClearContext() throws ServletException, IOException {
        // Given
        String expiredToken = Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .claim("role", "PATIENT")
                .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                .setExpiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(testKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(tokenProvider.getKey()).thenReturn(testKey);
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("When the token misses subject, it should not set authentication")
    void doFilterInternalWhenTokenMissesSubjectShouldNotAuthenticate() throws ServletException, IOException {
        // Given
        String tokenWithoutSubject = Jwts.builder()
                .claim("role", "PATIENT")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(testKey)
                .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenWithoutSubject);
        when(tokenProvider.getKey()).thenReturn(testKey);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
