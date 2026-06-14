package pl.MiASI.staff.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaffRoleUnitTest {

    @Test
    @DisplayName("StaffRole should contain DOCTOR and ADMIN_STAFF values")
    void staffRoleShouldContainCorrectValues() {
        // when
        StaffRole doctorRole = StaffRole.valueOf("DOCTOR");
        StaffRole adminRole = StaffRole.valueOf("ADMIN_STAFF");

        // then
        assertThat(doctorRole).isEqualTo(StaffRole.DOCTOR);
        assertThat(adminRole).isEqualTo(StaffRole.ADMIN_STAFF);
    }
}
