package pl.MiASI.staff.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StaffMemberUnitTest {

    @Test
    @DisplayName("Create should instantiate a new active StaffMember with given properties")
    void createWhenValidDataShouldCreateActiveStaffMember() {
        // given
        UUID id = UUID.randomUUID();
        StaffRole role = StaffRole.DOCTOR;
        String firstName = "Jan";
        String lastName = "Kowalski";
        String email = "jan.kowalski@example.com";
        String specialization = "Cardiology";
        String pwz = "1234567";
        String department = "Cardiology Dept";
        String position = "Senior Doctor";
        String workSchedule = "9-17";

        // when
        StaffMember staffMember = StaffMember.create(id, role, firstName, lastName, email, specialization, pwz, department, position, workSchedule);

        // then
        assertThat(staffMember.getId()).isEqualTo(id);
        assertThat(staffMember.getRole()).isEqualTo(role);
        assertThat(staffMember.getFirstName()).isEqualTo(firstName);
        assertThat(staffMember.getLastName()).isEqualTo(lastName);
        assertThat(staffMember.getEmail()).isEqualTo(email);
        assertThat(staffMember.isActive()).isTrue();
        assertThat(staffMember.getSpecialization()).isEqualTo(specialization);
        assertThat(staffMember.getPwz()).isEqualTo(pwz);
        assertThat(staffMember.getDepartment()).isEqualTo(department);
        assertThat(staffMember.getPosition()).isEqualTo(position);
        assertThat(staffMember.getWorkSchedule()).isEqualTo(workSchedule);
    }

    @Test
    @DisplayName("Update should modify the fields of StaffMember")
    void updateWhenValidDataShouldUpdateFields() {
        // given
        StaffMember staffMember = StaffMember.create(
                UUID.randomUUID(), StaffRole.ADMIN_STAFF, "Anna", "Nowak", "anna@test.com",
                null, null, "Administration", "Clerk", "8-16");

        String newFirstName = "Anna Maria";
        String newLastName = "Kowalczyk";
        String newEmail = "anna.kowalczyk@test.com";
        boolean newActive = false;
        String newSpecialization = "Management";
        String newPwz = null;
        String newDepartment = "HR";
        String newPosition = "Manager";
        String newWorkSchedule = "10-18";

        // when
        staffMember.update(newFirstName, newLastName, newEmail, newActive, newSpecialization, newPwz, newDepartment, newPosition, newWorkSchedule);

        // then
        assertThat(staffMember.getFirstName()).isEqualTo(newFirstName);
        assertThat(staffMember.getLastName()).isEqualTo(newLastName);
        assertThat(staffMember.getEmail()).isEqualTo(newEmail);
        assertThat(staffMember.isActive()).isEqualTo(newActive);
        assertThat(staffMember.getSpecialization()).isEqualTo(newSpecialization);
        assertThat(staffMember.getPwz()).isEqualTo(newPwz);
        assertThat(staffMember.getDepartment()).isEqualTo(newDepartment);
        assertThat(staffMember.getPosition()).isEqualTo(newPosition);
        assertThat(staffMember.getWorkSchedule()).isEqualTo(newWorkSchedule);
    }

    @Test
    @DisplayName("Deactivate should set the active flag to false")
    void deactivateWhenCalledShouldSetInactive() {
        // given
        StaffMember staffMember = StaffMember.create(
                UUID.randomUUID(), StaffRole.DOCTOR, "Piotr", "Zieliński", "piotr@test.com",
                "Neurology", "7654321", "Neurology Dept", "Doctor", "Shift");

        assertThat(staffMember.isActive()).isTrue();

        // when
        staffMember.deactivate();

        // then
        assertThat(staffMember.isActive()).isFalse();
    }
}
