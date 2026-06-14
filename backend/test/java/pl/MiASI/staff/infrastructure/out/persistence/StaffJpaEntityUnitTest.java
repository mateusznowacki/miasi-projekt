package pl.MiASI.staff.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StaffJpaEntityUnitTest {

    @Test
    @DisplayName("StaffJpaEntity when instantiated should allow getting and setting fields")
    void staffJpaEntityWhenInstantiatedShouldSetAndGetFields() {
        // given
        StaffJpaEntity entity = new StaffJpaEntity();
        UUID id = UUID.randomUUID();
        
        // when
        entity.setId(id);
        entity.setRole(StaffRole.DOCTOR);
        entity.setFirstName("Adam");
        entity.setLastName("Malysz");
        entity.setEmail("adam@test.com");
        entity.setActive(true);
        entity.setSpecialization("Surgery");
        entity.setPwz("9876543");
        entity.setDepartment("ER");
        entity.setPosition("Surgeon");
        entity.setWorkSchedule("10-18");
        
        // then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getRole()).isEqualTo(StaffRole.DOCTOR);
        assertThat(entity.getFirstName()).isEqualTo("Adam");
        assertThat(entity.getLastName()).isEqualTo("Malysz");
        assertThat(entity.getEmail()).isEqualTo("adam@test.com");
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getSpecialization()).isEqualTo("Surgery");
        assertThat(entity.getPwz()).isEqualTo("9876543");
        assertThat(entity.getDepartment()).isEqualTo("ER");
        assertThat(entity.getPosition()).isEqualTo("Surgeon");
        assertThat(entity.getWorkSchedule()).isEqualTo("10-18");
    }
}
