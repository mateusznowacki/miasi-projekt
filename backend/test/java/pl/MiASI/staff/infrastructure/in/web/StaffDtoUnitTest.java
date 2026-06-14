package pl.MiASI.staff.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StaffDtoUnitTest {

    @Test
    @DisplayName("StaffDto fromDomain should map StaffMember fields correctly")
    void staffDtoFromDomainShouldMapFieldsCorrectly() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember staffMember = StaffMember.create(
                id, StaffRole.DOCTOR, "Jan", "Kowalski", "jan@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        StaffDto dto = StaffDto.fromDomain(staffMember);

        // then
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.role()).isEqualTo("DOCTOR");
        assertThat(dto.firstName()).isEqualTo("Jan");
        assertThat(dto.lastName()).isEqualTo("Kowalski");
        assertThat(dto.email()).isEqualTo("jan@test.com");
        assertThat(dto.active()).isTrue();
        assertThat(dto.specialization()).isEqualTo("Cardiology");
        assertThat(dto.pwz()).isEqualTo("1234567");
        assertThat(dto.department()).isEqualTo("Dept");
        assertThat(dto.position()).isEqualTo("Doc");
        assertThat(dto.workSchedule()).isEqualTo("9-17");
    }
}
