package pl.MiASI.staff.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;
import pl.MiASI.staff.application.port.out.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceUnitTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AuthUseCase authUseCase;

    @InjectMocks
    private StaffService staffService;

    @Test
    @DisplayName("Create staff when role is DOCTOR and PWZ exists should throw exception")
    void createStaffWhenRoleDoctorAndPwzExistsShouldThrowException() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );
        when(staffRepository.existsByPwz(command.pwz())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> staffService.createStaff(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lekarz z podanym numerem PWZ już istnieje w bazie");
        verifyNoInteractions(authUseCase);
    }

    @Test
    @DisplayName("Create staff when email exists should throw exception")
    void createStaffWhenEmailExistsShouldThrowException() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.ADMIN_STAFF, "Admin", "User", "admin@test.com",
                null, null, "IT", "SysAdmin", "8-16"
        );
        when(staffRepository.existsByEmail(command.email())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> staffService.createStaff(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Użytkownik z podanym adresem email już istnieje w bazie");
        verifyNoInteractions(authUseCase);
    }

    @Test
    @DisplayName("Create staff when valid data should register user, create staff and return UUID")
    void createStaffWhenValidDataShouldCreateAndRegisterUserAndReturnId() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );
        UUID generatedId = UUID.randomUUID();
        AccountId accountId = new AccountId(generatedId);
        
        when(staffRepository.existsByPwz(command.pwz())).thenReturn(false);
        when(staffRepository.existsByEmail(command.email())).thenReturn(false);
        when(authUseCase.registerUser(eq(command.email()), anyString(), eq(Role.DOCTOR))).thenReturn(accountId);

        // when
        UUID resultId = staffService.createStaff(command);

        // then
        assertThat(resultId).isEqualTo(generatedId);
        verify(authUseCase).activateAccount(generatedId.toString());
        verify(staffRepository).save(any(StaffMember.class));
    }

    @Test
    @DisplayName("Update staff when staff not found should throw exception")
    void updateStaffWhenStaffNotFoundShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        UpdateStaffCommand command = new UpdateStaffCommand(
                "Jane", "Doe", "jane@test.com", true, null, null, null, null, null
        );
        when(staffRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> staffService.updateStaff(id, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Staff not found");
    }

    @Test
    @DisplayName("Update staff when role DOCTOR and PWZ exists for other ID should throw exception")
    void updateStaffWhenRoleDoctorAndPwzExistsForOtherIdShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.DOCTOR, "Old", "Name", "old@test.com", null, "1111111", null, null, null);
        UpdateStaffCommand command = new UpdateStaffCommand(
                "New", "Name", "new@test.com", true, null, "2222222", null, null, null
        );
        
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.existsByPwzAndIdNot("2222222", id)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> staffService.updateStaff(id, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lekarz z podanym numerem PWZ już istnieje w bazie");
    }

    @Test
    @DisplayName("Update staff when email exists for other ID should throw exception")
    void updateStaffWhenEmailExistsForOtherIdShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.ADMIN_STAFF, "Admin", "Old", "old@test.com", null, null, null, null, null);
        UpdateStaffCommand command = new UpdateStaffCommand(
                "Admin", "New", "exists@test.com", true, null, null, null, null, null
        );
        
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.existsByEmailAndIdNot("exists@test.com", id)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> staffService.updateStaff(id, command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Użytkownik z podanym adresem email już istnieje w bazie");
    }

    @Test
    @DisplayName("Update staff when valid data and active should update staff and email in IAM")
    void updateStaffWhenValidDataShouldUpdateStaffAndAuthEmail() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.DOCTOR, "Doc", "Smith", "old@test.com", null, "1234567", null, null, null);
        UpdateStaffCommand command = new UpdateStaffCommand(
                "Doc", "Smith", "new@test.com", true, "Pediatrics", "1234567", "Dept", "Pos", "Sch"
        );
        
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.existsByPwzAndIdNot("1234567", id)).thenReturn(false);
        when(staffRepository.existsByEmailAndIdNot("new@test.com", id)).thenReturn(false);

        // when
        staffService.updateStaff(id, command);

        // then
        assertThat(existingStaff.getEmail()).isEqualTo("new@test.com");
        verify(staffRepository).save(existingStaff);
        verify(authUseCase).updateEmail(any(AccountId.class), eq("new@test.com"));
        verify(authUseCase, never()).deactivateAccount(any(AccountId.class));
    }

    @Test
    @DisplayName("Update staff when valid data and inactive should update staff and deactivate account in IAM")
    void updateStaffWhenDeactivatedShouldDeactivateAuthAccount() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.ADMIN_STAFF, "Adm", "Smith", "adm@test.com", null, null, null, null, null);
        UpdateStaffCommand command = new UpdateStaffCommand(
                "Adm", "Smith", "adm@test.com", false, null, null, null, null, null
        );
        
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.existsByEmailAndIdNot("adm@test.com", id)).thenReturn(false);

        // when
        staffService.updateStaff(id, command);

        // then
        assertThat(existingStaff.isActive()).isFalse();
        verify(staffRepository).save(existingStaff);
        verify(authUseCase).deactivateAccount(any(AccountId.class));
    }

    @Test
    @DisplayName("Deactivate staff when not found should throw exception")
    void deactivateStaffWhenStaffNotFoundShouldThrowException() {
        // given
        UUID id = UUID.randomUUID();
        when(staffRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> staffService.deactivateStaff(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Staff not found");
    }

    @Test
    @DisplayName("Deactivate staff when found should deactivate and update IAM")
    void deactivateStaffWhenStaffFoundShouldDeactivateStaffAndAuthAccount() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.DOCTOR, "D", "S", "d@test.com", null, null, null, null, null);
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));

        // when
        staffService.deactivateStaff(id);

        // then
        assertThat(existingStaff.isActive()).isFalse();
        verify(staffRepository).save(existingStaff);
        verify(authUseCase).deactivateAccount(any(AccountId.class));
    }

    @Test
    @DisplayName("Delete staff should call repository delete")
    void deleteStaffWhenCalledShouldDeleteFromRepository() {
        // given
        UUID id = UUID.randomUUID();

        // when
        staffService.deleteStaff(id);

        // then
        verify(staffRepository).deleteById(id);
    }

    @Test
    @DisplayName("Get staff by ID when exists should return optional of staff")
    void getStaffByIdWhenExistsShouldReturnStaff() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember staff = StaffMember.create(id, StaffRole.DOCTOR, "A", "B", "c@test.com", null, null, null, null, null);
        when(staffRepository.findById(id)).thenReturn(Optional.of(staff));

        // when
        Optional<StaffMember> result = staffService.getStaffById(id);

        // then
        assertThat(result).isPresent().contains(staff);
    }

    @Test
    @DisplayName("Get all staff should return list from repository")
    void getAllStaffWhenCalledShouldReturnList() {
        // given
        StaffMember staff1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "A", "B", "c@test.com", null, null, null, null, null);
        StaffMember staff2 = StaffMember.create(UUID.randomUUID(), StaffRole.ADMIN_STAFF, "X", "Y", "z@test.com", null, null, null, null, null);
        when(staffRepository.findAll()).thenReturn(List.of(staff1, staff2));

        // when
        List<StaffMember> result = staffService.getAllStaff();

        // then
        assertThat(result).hasSize(2).containsExactly(staff1, staff2);
    }

    @Test
    @DisplayName("Get staff by role should return filtered list from repository")
    void getStaffByRoleWhenCalledShouldReturnListForRole() {
        // given
        StaffMember staff1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "A", "B", "c@test.com", null, null, null, null, null);
        when(staffRepository.findByRole(StaffRole.DOCTOR)).thenReturn(List.of(staff1));

        // when
        List<StaffMember> result = staffService.getStaffByRole(StaffRole.DOCTOR);

        // then
        assertThat(result).hasSize(1).containsExactly(staff1);
    }

    @Test
    @DisplayName("Search staff with various filters should return matching elements")
    void searchStaffWhenFiltersProvidedShouldReturnFiltered() {
        // given
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "John", "Doe", "doc1@test.com", "Cardiology", null, null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jane", "Smith", "doc2@test.com", "Neurology", null, null, null, null);
        StaffMember admin = StaffMember.create(UUID.randomUUID(), StaffRole.ADMIN_STAFF, "Admin", "User", "admin@test.com", null, null, null, null, null);
        admin.deactivate(); // inactive
        
        when(staffRepository.findAll()).thenReturn(List.of(doc1, doc2, admin));

        // when search by name Jane
        List<StaffMember> res1 = staffService.searchStaff("jane", null, null, null, null);
        assertThat(res1).hasSize(1).containsExactly(doc2);

        // when search by role DOCTOR
        List<StaffMember> res2 = staffService.searchStaff(null, null, null, "DOCTOR", null);
        assertThat(res2).hasSize(2).containsExactly(doc1, doc2);

        // when search by active status
        List<StaffMember> res3 = staffService.searchStaff(null, null, null, null, false);
        assertThat(res3).hasSize(1).containsExactly(admin);
        
        // when search by specialization
        List<StaffMember> res4 = staffService.searchStaff(null, null, "Cardio", null, null);
        assertThat(res4).hasSize(1).containsExactly(doc1);
    }

    @Test
    @DisplayName("Create staff when role is not DOCTOR should bypass PWZ existence check")
    void createStaffWhenRoleNotDoctorShouldBypassPwzCheck() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.ADMIN_STAFF, "John", "Doe", "john@test.com",
                null, "1234567", "Dept", "Admin", "9-17"
        );
        UUID generatedId = UUID.randomUUID();
        AccountId accountId = new AccountId(generatedId);
        
        when(staffRepository.existsByEmail(command.email())).thenReturn(false);
        when(authUseCase.registerUser(eq(command.email()), anyString(), eq(Role.ADMIN_STAFF))).thenReturn(accountId);

        // when
        UUID resultId = staffService.createStaff(command);

        // then
        assertThat(resultId).isEqualTo(generatedId);
        verify(staffRepository, never()).existsByPwz(anyString());
        verify(staffRepository).save(any(StaffMember.class));
    }

    @Test
    @DisplayName("Update staff when role is not DOCTOR should bypass PWZ existence check")
    void updateStaffWhenRoleNotDoctorShouldBypassPwzCheck() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember existingStaff = StaffMember.create(id, StaffRole.ADMIN_STAFF, "Old", "Name", "old@test.com", null, "1111111", null, null, null);
        UpdateStaffCommand command = new UpdateStaffCommand(
                "New", "Name", "old@test.com", true, null, "2222222", null, null, null
        );
        
        when(staffRepository.findById(id)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.existsByEmailAndIdNot("old@test.com", id)).thenReturn(false);

        // when
        staffService.updateStaff(id, command);

        // then
        verify(staffRepository, never()).existsByPwzAndIdNot(anyString(), any(UUID.class));
        verify(staffRepository).save(existingStaff);
    }
}
