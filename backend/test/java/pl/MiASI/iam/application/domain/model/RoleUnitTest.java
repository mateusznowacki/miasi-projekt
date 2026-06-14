package pl.MiASI.iam.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleUnitTest {

    @Test
    @DisplayName("When evaluating Role enum, all required roles should be present")
    void roleEnumShouldHaveExpectedValues() {
        // Then
        assertNotNull(Role.valueOf("PATIENT"));
        assertNotNull(Role.valueOf("DOCTOR"));
        assertNotNull(Role.valueOf("ADMIN_STAFF"));
        assertNotNull(Role.valueOf("ADMIN"));
    }

    @Test
    @DisplayName("When counting roles, there should be exactly four")
    void roleEnumShouldHaveExactlyFourValues() {
        // Then
        assertEquals(4, Role.values().length);
    }
}
