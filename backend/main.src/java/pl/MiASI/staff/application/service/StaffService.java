package pl.MiASI.staff.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.out.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService implements StaffUseCase {

    private final StaffRepository staffRepository;
    private final AuthUseCase authUseCase;

    @Override
    @Transactional
    public UUID createStaff(CreateStaffCommand command) {
        if (command.role() == StaffRole.DOCTOR) {
            if (command.pwz() != null && staffRepository.existsByPwz(command.pwz())) {
                throw new IllegalArgumentException("Lekarz z podanym numerem PWZ już istnieje w bazie");
            }
        }

        if (staffRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem email już istnieje w bazie");
        }
        Role iamRole = mapToIamRole(command.role());
        String defaultPassword = "password"; // Prototype
        AccountId accountId = authUseCase.registerUser(command.email(), defaultPassword, iamRole);
        authUseCase.activateAccount(accountId.value().toString()); // Aktywujemy od razu dla uproszczenia

        StaffMember staff = StaffMember.create(
                accountId.value(),
                command.role(),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.specialization(),
                command.pwz(),
                command.department(),
                command.position(),
                command.workSchedule()
        );
        staffRepository.save(staff);
        return staff.getId();
    }

    @Override
    @Transactional
    public void updateStaff(UUID id, UpdateStaffCommand command) {
        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        if (staff.getRole() == StaffRole.DOCTOR) {
            if (command.pwz() != null && staffRepository.existsByPwzAndIdNot(command.pwz(), id)) {
                throw new IllegalArgumentException("Lekarz z podanym numerem PWZ już istnieje w bazie");
            }
        }

        if (command.email() != null && staffRepository.existsByEmailAndIdNot(command.email(), id)) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem email już istnieje w bazie");
        }

        staff.update(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.active(),
                command.specialization(),
                command.pwz(),
                command.department(),
                command.position(),
                command.workSchedule()
        );
        staffRepository.save(staff);

        authUseCase.updateEmail(new AccountId(id), command.email());
        if (!command.active()) {
            authUseCase.deactivateAccount(new AccountId(id));
        }
    }

    @Override
    @Transactional
    public void deactivateStaff(UUID id) {
        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        staff.deactivate();
        staffRepository.save(staff);

        authUseCase.deactivateAccount(new AccountId(id));
    }

    @Override
    @Transactional
    public void deleteStaff(UUID id) {
        staffRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffMember> getStaffById(UUID id) {
        return staffRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> getStaffByRole(StaffRole role) {
        return staffRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> searchStaff(String firstName, String lastName, String specialization, String role, Boolean active) {
        return getAllStaff().stream()
                .filter(s -> firstName == null || firstName.isBlank() || s.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .filter(s -> lastName == null || lastName.isBlank() || s.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                .filter(s -> specialization == null || specialization.isBlank() || (s.getSpecialization() != null && s.getSpecialization().toLowerCase().contains(specialization.toLowerCase())))
                .filter(s -> role == null || role.isBlank() || (s.getRole() != null && s.getRole().name().equalsIgnoreCase(role)))
                .filter(s -> active == null || s.isActive() == active)
                .toList();
    }

    private Role mapToIamRole(StaffRole staffRole) {
        return switch (staffRole) {
            case DOCTOR -> Role.DOCTOR;
            case ADMIN_STAFF -> Role.ADMIN_STAFF;
            default -> throw new IllegalArgumentException("Unknown role");
        };
    }
}