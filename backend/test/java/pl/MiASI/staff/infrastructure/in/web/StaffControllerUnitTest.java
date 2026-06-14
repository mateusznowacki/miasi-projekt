package pl.MiASI.staff.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffControllerUnitTest {

    @Mock
    private StaffUseCase staffUseCase;

    @InjectMocks
    private StaffController staffController;

    @Test
    @DisplayName("List staff when no role provided should return all staff members")
    void listStaffWhenNoRoleShouldReturnAll() {
        // given
        StaffMember staff = StaffMember.create(UUID.randomUUID(), StaffRole.ADMIN_STAFF, "John", "Doe", "john@test.com", null, null, null, null, null);
        when(staffUseCase.getAllStaff()).thenReturn(List.of(staff));

        // when
        ResponseEntity<List<StaffDto>> response = staffController.listStaff(null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).firstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("List staff when role provided should return filtered staff members")
    void listStaffWhenRoleProvidedShouldReturnForRole() {
        // given
        StaffMember doc = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Doc", "Smith", "doc@test.com", null, null, null, null, null);
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc));

        // when
        ResponseEntity<List<StaffDto>> response = staffController.listStaff("DOCTOR");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).role()).isEqualTo("DOCTOR");
    }

    @Test
    @DisplayName("Create staff when valid command should return 201 Created and staff ID")
    void createStaffWhenValidCommandShouldReturnCreatedStatusAndId() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(StaffRole.ADMIN_STAFF, "Anna", "Nowak", "anna@test.com", null, null, null, null, null);
        UUID expectedId = UUID.randomUUID();
        when(staffUseCase.createStaff(command)).thenReturn(expectedId);

        // when
        ResponseEntity<Map<String, String>> response = staffController.createStaff(command);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("staffId", expectedId.toString());
    }

    @Test
    @DisplayName("Get staff when exists should return 200 OK and staff member")
    void getStaffWhenExistsShouldReturnStaff() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember staff = StaffMember.create(id, StaffRole.DOCTOR, "Test", "Test", "test@test.com", null, null, null, null, null);
        when(staffUseCase.getStaffById(id)).thenReturn(Optional.of(staff));

        // when
        ResponseEntity<StaffDto> response = staffController.getStaff(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Get staff when does not exist should return 404 Not Found")
    void getStaffWhenNotExistsShouldReturnNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(staffUseCase.getStaffById(id)).thenReturn(Optional.empty());

        // when
        ResponseEntity<StaffDto> response = staffController.getStaff(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Update staff when valid command should return 200 OK")
    void updateStaffWhenValidCommandShouldReturnOk() {
        // given
        UUID id = UUID.randomUUID();
        UpdateStaffCommand command = new UpdateStaffCommand("John", "Doe", "test@test.com", true, null, null, null, null, null);

        // when
        ResponseEntity<Void> response = staffController.updateStaff(id, command);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(staffUseCase).updateStaff(id, command);
    }

    @Test
    @DisplayName("Deactivate staff should return 200 OK")
    void deactivateStaffWhenCalledShouldReturnOk() {
        // given
        UUID id = UUID.randomUUID();

        // when
        ResponseEntity<Void> response = staffController.deactivateStaff(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(staffUseCase).deactivateStaff(id);
    }

    @Test
    @DisplayName("Delete staff should return 204 No Content")
    void deleteStaffWhenCalledShouldReturnNoContent() {
        // given
        UUID id = UUID.randomUUID();

        // when
        ResponseEntity<Void> response = staffController.deleteStaff(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(staffUseCase).deleteStaff(id);
    }

    @Test
    @DisplayName("Search staff should return matching list")
    void searchStaffWhenCalledShouldReturnMatchingList() {
        // given
        StaffMember staff = StaffMember.create(UUID.randomUUID(), StaffRole.ADMIN_STAFF, "John", "Doe", "john@test.com", null, null, null, null, null);
        when(staffUseCase.searchStaff("John", null, null, null, null)).thenReturn(List.of(staff));

        // when
        ResponseEntity<List<StaffDto>> response = staffController.searchStaff("John", null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).firstName()).isEqualTo("John");
    }
}
