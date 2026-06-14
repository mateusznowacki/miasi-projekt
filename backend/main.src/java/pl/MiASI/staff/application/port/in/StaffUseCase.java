package pl.MiASI.staff.application.port.in;

import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffUseCase {
    UUID createStaff(CreateStaffCommand command);

    void updateStaff(UUID id, UpdateStaffCommand command);

    void deactivateStaff(UUID id);

    void deleteStaff(UUID id);

    Optional<StaffMember> getStaffById(UUID id);

    List<StaffMember> getAllStaff();

    List<StaffMember> getStaffByRole(StaffRole role);

    List<StaffMember> searchStaff(String firstName, String lastName, String specialization, String role, Boolean active);
}